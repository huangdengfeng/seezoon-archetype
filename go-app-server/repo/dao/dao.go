package dao

import (
	"context"
	"database/sql"

	"gorm.io/gorm"
)

const (
	DbRecordStatusValid   int8 = 1 // 有效
	DbRecordStatusInvalid int8 = 2 // 无效
)

type ctxKey struct{}

var defaultDB *gorm.DB

func SetDefaultDB(db *gorm.DB) {
	defaultDB = db
}

func WithDB(ctx context.Context, db *gorm.DB) context.Context {
	return context.WithValue(ctx, ctxKey{}, db)
}

func DB(ctx context.Context) *gorm.DB {
	var db *gorm.DB
	if d, ok := ctx.Value(ctxKey{}).(*gorm.DB); ok && d != nil {
		db = d
	} else {
		db = defaultDB
	}
	return db.WithContext(ctx)
}

// DoTransaction runs fn in a database transaction. Use DB(ctx) inside fn to access tx.
func DoTransaction(ctx context.Context, fn func(ctx context.Context) error, opts ...*sql.TxOptions) error {
	return DB(ctx).Transaction(func(tx *gorm.DB) error {
		return fn(WithDB(ctx, tx))
	}, opts...)
}
