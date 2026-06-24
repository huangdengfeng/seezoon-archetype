package server

import (
	"context"
	"errors"
	"log/slog"
	"net/http"
	"os"
	"time"
)

const shutdownTimeout = 30 * time.Second

func Start(handler http.Handler, addr string) *http.Server {
	srv := &http.Server{
		Addr:    addr,
		Handler: handler,
	}
	go func() {
		slog.Info("http server started", "addr", addr)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			slog.Error("http server failed", "err", err)
			os.Exit(1)
		}
	}()
	return srv
}

func Shutdown(srv *http.Server) error {
	ctx, cancel := context.WithTimeout(context.Background(), shutdownTimeout)
	defer cancel()
	return srv.Shutdown(ctx)
}
