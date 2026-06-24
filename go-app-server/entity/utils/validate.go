package utils

import (
	"log/slog"
	"reflect"
	"time"

	"app-server/entity/errorx"

	"github.com/go-playground/validator/v10"
)

var validate = validator.New()

func init() {
	_ = validate.RegisterValidation("date", validateDate)
}

func validateDate(fl validator.FieldLevel) bool {
	v := fl.Field()
	if v.Kind() == reflect.Ptr {
		if v.IsNil() {
			return true
		}
		v = v.Elem()
	}
	if v.Kind() != reflect.String {
		return false
	}
	s := v.String()
	if s == "" {
		return true
	}
	_, err := time.ParseInLocation(time.DateOnly, s, time.Local)
	return err == nil
}

func Validate(v any) error {
	if v == nil {
		slog.Error("validate failed", "reason", "request is required")
		return errorx.New(errorx.BadArgs.Code, errorx.BadArgs.Msg, "request is required")
	}
	rv := reflect.ValueOf(v)
	if rv.Kind() == reflect.Ptr && rv.IsNil() {
		slog.Error("validate failed", "reason", "request is required")
		return errorx.New(errorx.BadArgs.Code, errorx.BadArgs.Msg, "request is required")
	}

	if err := validate.Struct(v); err != nil {
		slog.Error("validate failed", "err", err)
		return errorx.New(errorx.BadArgs.Code, errorx.BadArgs.Msg, err)
	}
	return nil
}
