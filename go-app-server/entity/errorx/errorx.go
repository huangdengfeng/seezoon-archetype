package errorx

import (
	stderrors "errors"
	"fmt"
	"log/slog"
)

var Unknown = New(1000, "system errors:%s")
var BadArgs = New(1001, "bad args:%s")
var SqlError = New(1002, "sql errors:%s")
var RowsAffectedNotMatch = New(1003, "rows affected:%d")

type Error struct {
	Code int
	Msg  string
}

func (e *Error) Error() string {
	return fmt.Sprintf("[%d] %s", e.Code, e.Msg)
}

func New(code int, msg string, args ...any) *Error {
	if len(args) > 0 {
		msg = fmt.Sprintf(msg, args...)
	}
	return &Error{Code: code, Msg: msg}
}

func With(e *Error, args ...any) *Error {
	msg := e.Msg
	if len(args) > 0 {
		msg = fmt.Sprintf(msg, args...)
	}
	err := &Error{Code: e.Code, Msg: msg}
	logArgs := []any{"code", err.Code, "msg", err.Msg}
	for _, arg := range args {
		if cause, ok := arg.(error); ok {
			logArgs = append(logArgs, "cause", cause)
		}
	}
	if e.Code == BadArgs.Code {
		slog.Warn("biz error", logArgs...)
	} else {
		slog.Error("biz error", logArgs...)
	}
	return err
}

func As(err error) (*Error, bool) {
	var e *Error
	if stderrors.As(err, &e) {
		return e, true
	}
	return nil, false
}
