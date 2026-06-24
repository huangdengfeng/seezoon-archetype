package tests

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"app-server/entity/response"
	studentsvc "app-server/service/student"
)

func TestStudentPage_Success(t *testing.T) {
	no := fmt.Sprintf("PAGE%d", time.Now().UnixNano())
	addResp := doJSON(t, http.MethodPost, "/student/add", map[string]any{
		"no":   no,
		"name": "分页查询",
		"sex":  2,
	})
	if addResp.Code != http.StatusCreated {
		t.Fatalf("setup add status=%d body=%s", addResp.Code, addResp.Body.String())
	}

	w := doJSON(t, http.MethodPost, "/student/page", map[string]any{
		"page":     1,
		"pageSize": 10,
		"no":       no,
	})

	if w.Code != http.StatusOK {
		t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
	}

	var page response.Page[*studentsvc.StudentCO]
	if err := json.Unmarshal(w.Body.Bytes(), &page); err != nil {
		t.Fatal(err)
	}
	if page.Total < 1 {
		t.Fatalf("total=%d", page.Total)
	}
	if len(page.Data) == 0 {
		t.Fatal("expected data")
	}
	if page.Data[0].No != no {
		t.Fatalf("no=%s", page.Data[0].No)
	}
}

func TestStudentPage_Empty(t *testing.T) {
	w := doJSON(t, http.MethodPost, "/student/page", map[string]any{
		"page":     1,
		"pageSize": 10,
		"no":       fmt.Sprintf("NONE%d", time.Now().UnixNano()),
	})

	if w.Code != http.StatusOK {
		t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
	}

	var page response.Page[*studentsvc.StudentCO]
	if err := json.Unmarshal(w.Body.Bytes(), &page); err != nil {
		t.Fatal(err)
	}
	if page.Total != 0 {
		t.Fatalf("total=%d", page.Total)
	}
	if len(page.Data) != 0 {
		t.Fatalf("data len=%d", len(page.Data))
	}
}

func TestStudentPage_InvalidJSON(t *testing.T) {
	r := routerOrSkip(t)
	req := httptest.NewRequest(http.MethodPost, "/student/page", bytes.NewBufferString("{"))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
	}
}
