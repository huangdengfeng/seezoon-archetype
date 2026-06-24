package config

import (
	"fmt"
	"io"
	"log/slog"
	"os"
	"path/filepath"
	"strings"

	"gopkg.in/natefinch/lumberjack.v2"
)

func initDefaultLog() {
	slog.SetDefault(slog.New(slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{
		Level: slog.LevelInfo,
	})))
}

func initLog(cfg *LogConfig) error {
	if cfg == nil {
		return fmt.Errorf("log config is required")
	}
	slog.SetDefault(newLogger(cfg))
	return nil
}

func newLogger(cfg *LogConfig) *slog.Logger {
	writer, err := newWriter(cfg)
	if err != nil {
		slog.Error("init log writer failed, fallback to stderr", "err", err)
		writer = os.Stderr
	}
	return slog.New(slog.NewTextHandler(writer, &slog.HandlerOptions{
		Level:     parseLevel(cfg.Level),
		AddSource: cfg.AddSource,
	}))
}

func newWriter(cfg *LogConfig) (io.Writer, error) {
	var writers []io.Writer
	if cfg.Stdout || cfg.Path == "" {
		writers = append(writers, os.Stdout)
	}
	if cfg.Path != "" {
		if err := os.MkdirAll(filepath.Dir(cfg.Path), 0o755); err != nil {
			return nil, err
		}
		writers = append(writers, &lumberjack.Logger{
			Filename:   cfg.Path,
			MaxSize:    cfg.MaxSize,
			MaxBackups: cfg.MaxBackups,
			MaxAge:     cfg.MaxAge,
			Compress:   cfg.Compress,
		})
	}
	if len(writers) == 1 {
		return writers[0], nil
	}
	return io.MultiWriter(writers...), nil
}

func parseLevel(level string) slog.Level {
	switch strings.ToLower(level) {
	case "debug":
		return slog.LevelDebug
	case "warn", "warning":
		return slog.LevelWarn
	case "error":
		return slog.LevelError
	default:
		return slog.LevelInfo
	}
}
