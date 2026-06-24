package main

import (
	"flag"
	"fmt"
	"log/slog"
	"os"
	"os/signal"
	"path/filepath"
	"syscall"

	"app-server/entity/config"
	"app-server/logic"
	"app-server/repo/dao"
	"app-server/server"
	"app-server/service"
	studentsvc "app-server/service/student"
)

func main() {
	confDir := flag.String("conf", "conf", "config directory")
	flag.Parse()

	if err := config.Init(filepath.Join(*confDir, "config.yaml")); err != nil {
		slog.Error("init failed", "err", err)
		os.Exit(1)
	}
	defer config.Shutdown()

	studentInfoDao := dao.NewStudentInfoDao()
	studentService := logic.NewStudentService(studentInfoDao)

	r := service.NewRouter(service.Exes{
		AddStudentCmdExe:  studentsvc.NewAddStudentCmdExe(studentService),
		StudentPageQryExe: studentsvc.NewStudentPageQryExe(studentInfoDao),
	})

	addr := fmt.Sprintf(":%d", config.GetConfig().Server.Port)
	httpServer := server.Start(r, addr)

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
	<-quit

	slog.Info("application stopping")
	if err := server.Shutdown(httpServer); err != nil {
		slog.Error("server shutdown failed", "err", err)
		os.Exit(1)
	}
	slog.Info("application stopped")
}
