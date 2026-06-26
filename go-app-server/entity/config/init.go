package config

import (
	"log/slog"

	"gorm.io/gorm"
)

var (
	cfg *Config
	db  *gorm.DB
)

func init() {
	initDefaultLog()
}

func Init(configPath string) error {
	var err error
	cfg, err = load(configPath)
	if err != nil {
		return err
	}
	if err := initLog(cfg.Log); err != nil {
		slog.Error("init logger failed", "err", err)
		return err
	}
	db, err = openDB(cfg.DB)
	if err != nil {
		return err
	}
	return nil
}

func Shutdown() {
	if db == nil {
		return
	}
	sqlDB, err := db.DB()
	if err != nil {
		slog.Error("close db failed", "err", err)
		return
	}
	if err := sqlDB.Close(); err != nil {
		slog.Error("close db failed", "err", err)
	}
}

func GetDb() *gorm.DB {
	return db
}

func GetConfig() *Config {
	return cfg
}
