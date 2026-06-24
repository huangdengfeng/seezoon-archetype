package errorx

import (
	stderrors "errors"
	"io"
	"log/slog"
	"os"
	"testing"
)

func TestMain(m *testing.M) {
	slog.SetDefault(slog.New(slog.NewTextHandler(io.Discard, nil)))
	os.Exit(m.Run())
}

func TestNew(t *testing.T) {
	err := New(1001, "invalid param")
	if err.Code != 1001 {
		t.Fatalf("unexpected code: %d", err.Code)
	}
	if err.Msg != "invalid param" {
		t.Fatalf("unexpected msg: %s", err.Msg)
	}
	if err.Error() != "[1001] invalid param" {
		t.Fatalf("unexpected error string: %s", err.Error())
	}
}

func TestNewWithArgs(t *testing.T) {
	err := New(1002, "invalid param: %s", "name")
	if err.Msg != "invalid param: name" {
		t.Fatalf("unexpected msg: %s", err.Msg)
	}
	if err.Error() != "[1002] invalid param: name" {
		t.Fatalf("unexpected error string: %s", err.Error())
	}
}

func TestWith(t *testing.T) {
	err := With(SqlError, "connection refused")
	if err.Code != 1002 || err.Msg != "sql errors:connection refused" {
		t.Fatalf("unexpected error: %+v", err)
	}

	err = With(RowsAffectedNotMatch, 0)
	if err.Code != 1003 || err.Msg != "rows affected:0" {
		t.Fatalf("unexpected error: %+v", err)
	}
}

func TestAs(t *testing.T) {
	err := New(1001, "invalid param")

	got, ok := As(err)
	if !ok {
		t.Fatal("expected As to succeed")
	}
	if got.Code != 1001 || got.Msg != "invalid param" {
		t.Fatalf("unexpected error: %+v", got)
	}

	_, ok = As(stderrors.New("other error"))
	if ok {
		t.Fatal("expected As to fail for non-biz error")
	}
}
