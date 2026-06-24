package config

import (
	"context"
	"encoding/json"
	"fmt"
	"log/slog"
	"os"
	"strings"

	"github.com/spf13/viper"
)

type Config struct {
	DB     *DBConfig     `mapstructure:"db"`
	Log    *LogConfig    `mapstructure:"log"`
	Server *ServerConfig `mapstructure:"server"`
}

type ServerConfig struct {
	Port int `mapstructure:"port"`
}

type LogConfig struct {
	Level      string `mapstructure:"level"`
	Path       string `mapstructure:"path"`
	MaxSize    int    `mapstructure:"maxSize"` // MB
	MaxBackups int    `mapstructure:"maxBackups"`
	MaxAge     int    `mapstructure:"maxAge"` // days
	Compress   bool   `mapstructure:"compress"`
	Stdout     bool   `mapstructure:"stdout"`
	AddSource  bool   `mapstructure:"addSource"`
}

type DBConfig struct {
	DSN             string `mapstructure:"dsn" json:"-"`
	User            string `mapstructure:"user"`
	Password        string `mapstructure:"password" json:"-"`
	MaxOpenConns    int    `mapstructure:"maxOpenConns"`
	MaxIdleConns    int    `mapstructure:"maxIdleConns"`
	ConnMaxLifetime int    `mapstructure:"connMaxLifetime"` // seconds
	ConnMaxIdleTime int    `mapstructure:"connMaxIdleTime"` // seconds
}

func (d *DBConfig) ResolvedDSN() string {
	return fmt.Sprintf(d.DSN, d.User, d.Password)
}

// Load reads config from path. Placeholders like ${DB_USER} are expanded from environment variables.
func load(path string) (*Config, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		slog.Error("read config file failed", "path", path, "err", err)
		return nil, fmt.Errorf("read config file: %w", err)
	}

	v := viper.New()
	v.SetConfigType("yaml")
	if err := v.ReadConfig(strings.NewReader(os.ExpandEnv(string(data)))); err != nil {
		slog.Error("parse config file failed", "path", path, "err", err)
		return nil, fmt.Errorf("parse config file: %w", err)
	}

	var cfg Config
	if err := v.Unmarshal(&cfg); err != nil {
		slog.Error("unmarshal config failed", "path", path, "err", err)
		return nil, fmt.Errorf("parse config: %w", err)
	}
	if cfg.DB == nil {
		slog.Error("config missing db section", "path", path)
		return nil, fmt.Errorf("parse config: missing db section")
	}
	if cfg.Log == nil {
		slog.Error("config missing log section", "path", path)
		return nil, fmt.Errorf("parse config: missing log section")
	}
	if cfg.Server == nil {
		cfg.Server = &ServerConfig{Port: 8080}
	}

	b, err := json.Marshal(&cfg)
	if err != nil {
		slog.Error("marshal config failed", "err", err)
		return nil, fmt.Errorf("marshal config: %w", err)
	}
	slog.InfoContext(context.Background(), "config loaded", "config", string(b))
	return &cfg, nil
}
