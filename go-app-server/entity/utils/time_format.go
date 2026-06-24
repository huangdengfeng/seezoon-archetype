package utils

import (
	"time"

	"app-server/entity/errorx"
)

func ParseDate(s *string) (*time.Time, error) {
	if s == nil || *s == "" {
		return nil, nil
	}
	t, err := time.ParseInLocation(time.DateOnly, *s, time.Local)
	if err != nil {
		return nil, errorx.New(errorx.BadArgs.Code, errorx.BadArgs.Msg, "birthday format must be "+time.DateOnly)
	}
	return &t, nil
}

func FormatDate(t *time.Time) *string {
	if t == nil {
		return nil
	}
	s := t.In(time.Local).Format(time.DateOnly)
	return &s
}

func FormatDateTime(t time.Time) string {
	return t.In(time.Local).Format(time.DateTime)
}
