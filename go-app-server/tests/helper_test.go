package tests

import (
	"bytes"
	"encoding/json"
	"io"
	"net/http/httptest"
	"os"
	"path/filepath"
	"sync"
	"testing"

	"app-server/entity/config"
	"app-server/logic"
	"app-server/repo/dao"
	"app-server/service"
	studentsvc "app-server/service/student"

	"github.com/gin-gonic/gin"
)

var (
	setupOnce sync.Once
	router    *gin.Engine
	setupErr  error
)

func TestMain(m *testing.M) {
	gin.SetMode(gin.TestMode)
	code := m.Run()
	config.Shutdown()
	os.Exit(code)
}

func routerOrSkip(t *testing.T) *gin.Engine {
	t.Helper()
	setupOnce.Do(func() {
		configPath := filepath.Join("..", "conf", "config.yaml")
		setupErr = config.Init(configPath)
		if setupErr != nil {
			return
		}
		studentInfoDao := dao.NewStudentInfoDao()
		studentService := logic.NewStudentService(studentInfoDao)
		router = service.NewRouter(service.Exes{
			AddStudentCmdExe:  studentsvc.NewAddStudentCmdExe(studentService),
			StudentPageQryExe: studentsvc.NewStudentPageQryExe(studentInfoDao),
		})
	})
	if setupErr != nil {
		t.Skipf("api test skipped: %v", setupErr)
	}
	return router
}

func doJSON(t *testing.T, method, path string, body any) *httptest.ResponseRecorder {
	t.Helper()
	r := routerOrSkip(t)
	var reader io.Reader
	if body != nil {
		b, err := json.Marshal(body)
		if err != nil {
			t.Fatal(err)
		}
		reader = bytes.NewReader(b)
	}
	req := httptest.NewRequest(method, path, reader)
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)
	return w
}
