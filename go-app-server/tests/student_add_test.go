package tests

import (
	"encoding/json"
	"fmt"
	"net/http"
	"testing"
	"time"

	"app-server/entity/errorx"
	"app-server/entity/response"
	studentsvc "app-server/service/student"
)

func TestStudentAdd_Success(t *testing.T) {
	no := fmt.Sprintf("API%d", time.Now().UnixNano())
	w := doJSON(t, http.MethodPost, "/student/add", map[string]any{
		"no":   no,
		"name": "接口测试",
		"sex":  1,
	})

	if w.Code != http.StatusCreated {
		t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
	}

	var co studentsvc.StudentCO
	if err := json.Unmarshal(w.Body.Bytes(), &co); err != nil {
		t.Fatal(err)
	}
	if co.ID == 0 {
		t.Fatal("expected id")
	}
	if co.No != no {
		t.Fatalf("no=%s", co.No)
	}
	if co.Name != "接口测试" {
		t.Fatalf("name=%s", co.Name)
	}
	if co.CreateTime == "" || co.UpdateTime == "" {
		t.Fatal("expected createTime and updateTime")
	}
}

func TestStudentAdd_ValidateFailed(t *testing.T) {
	w := doJSON(t, http.MethodPost, "/student/add", map[string]any{
		"name": "缺少学号",
	})

	if w.Code != http.StatusBadRequest {
		t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
	}
	if ct := w.Header().Get("Content-Type"); ct != response.ProblemJSON {
		t.Fatalf("content-type=%s", ct)
	}

	var problem response.Problem
	if err := json.Unmarshal(w.Body.Bytes(), &problem); err != nil {
		t.Fatal(err)
	}
	if problem.Code != errorx.BadArgs.Code {
		t.Fatalf("code=%d", problem.Code)
	}
}

func TestStudentAdd_DuplicateNo(t *testing.T) {
	no := fmt.Sprintf("DUP%d", time.Now().UnixNano())
	body := map[string]any{
		"no":   no,
		"name": "重复学号",
		"sex":  1,
	}

	w := doJSON(t, http.MethodPost, "/student/add", body)
	if w.Code != http.StatusCreated {
		t.Fatalf("first add status=%d body=%s", w.Code, w.Body.String())
	}

	w = doJSON(t, http.MethodPost, "/student/add", body)
	if w.Code != http.StatusBadRequest {
		t.Fatalf("duplicate status=%d body=%s", w.Code, w.Body.String())
	}

	var problem response.Problem
	if err := json.Unmarshal(w.Body.Bytes(), &problem); err != nil {
		t.Fatal(err)
	}
	if problem.Code != errorx.BadArgs.Code {
		t.Fatalf("code=%d", problem.Code)
	}
}
