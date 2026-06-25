package tests

import (
	"fmt"
	"net/http"
	"testing"
	"time"

	"app-server/entity/errorx"
	"app-server/entity/response"
	studentsvc "app-server/service/student"

	"github.com/stretchr/testify/require"
)

func TestStudentAdd_Success(t *testing.T) {
	no := fmt.Sprintf("API%d", time.Now().UnixNano())
	resp := postJSON(t, "/student/add", map[string]any{
		"no":   no,
		"name": "接口测试",
		"sex":  1,
	})
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusOK)

	var co studentsvc.StudentCO
	decodeJSON(t, resp, &co)
	require.NotZero(t, co.ID)
	require.Equal(t, no, co.No)
	require.Equal(t, "接口测试", co.Name)
	require.NotEmpty(t, co.CreateTime)
	require.NotEmpty(t, co.UpdateTime)
}

func TestStudentAdd_ValidateFailed(t *testing.T) {
	resp := postJSON(t, "/student/add", map[string]any{
		"name": "缺少学号",
	})
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusBadRequest)
	require.Equal(t, response.ProblemJSON, resp.Header.Get("Content-Type"))

	var problem response.Problem
	decodeJSON(t, resp, &problem)
	require.Equal(t, errorx.BadArgs.Code, problem.Code)
}

func TestStudentAdd_DuplicateNo(t *testing.T) {
	no := fmt.Sprintf("DUP%d", time.Now().UnixNano())
	body := map[string]any{
		"no":   no,
		"name": "重复学号",
		"sex":  1,
	}

	resp := postJSON(t, "/student/add", body)
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusOK)

	resp = postJSON(t, "/student/add", body)
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusBadRequest)

	var problem response.Problem
	decodeJSON(t, resp, &problem)
	require.Equal(t, errorx.BadArgs.Code, problem.Code)
}
