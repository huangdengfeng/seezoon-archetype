package utils

import (
	"io"
	"log/slog"
	"os"
	"testing"

	"app-server/entity/errorx"
)

func TestMain(m *testing.M) {
	slog.SetDefault(slog.New(slog.NewTextHandler(io.Discard, nil)))
	os.Exit(m.Run())
}

type sampleVO struct {
	Name string `validate:"required"`
}

func TestValidate(t *testing.T) {
	if err := Validate(&sampleVO{Name: "ok"}); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	err := Validate(&sampleVO{})
	if err == nil {
		t.Fatal("expected validation error")
	}
	e, ok := errorx.As(err)
	if !ok || e.Code != errorx.BadArgs.Code {
		t.Fatalf("unexpected error: %v", err)
	}

	err = Validate((*sampleVO)(nil))
	if err == nil {
		t.Fatal("expected nil request error")
	}
}
