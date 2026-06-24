package config

import (
	"app-server/entity/errorx"
	"time"

	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

func openDB(cfg *DBConfig) (*gorm.DB, error) {
	db, err := gorm.Open(mysql.Open(cfg.ResolvedDSN()), &gorm.Config{})
	if err != nil {
		return nil, errorx.With(errorx.SqlError, err)
	}

	sqlDB, err := db.DB()
	if err != nil {
		return nil, errorx.With(errorx.SqlError, err)
	}
	sqlDB.SetMaxOpenConns(cfg.MaxOpenConns)
	sqlDB.SetMaxIdleConns(cfg.MaxIdleConns)
	sqlDB.SetConnMaxLifetime(time.Duration(cfg.ConnMaxLifetime) * time.Second)
	sqlDB.SetConnMaxIdleTime(time.Duration(cfg.ConnMaxIdleTime) * time.Second)
	if err := sqlDB.Ping(); err != nil {
		return nil, errorx.With(errorx.SqlError, err)
	}
	return db, nil
}
