package tests

import (
	"bytes"
	"fmt"
	"net/http"
	"testing"
	"time"

	"app-server/entity/response"
	studentsvc "app-server/service/student"

	"github.com/stretchr/testify/require"
)

func TestStudentPage_Success(t *testing.T) {
	no := fmt.Sprintf("PAGE%d", time.Now().UnixNano())
	addResp := postJSON(t, "/student/add", map[string]any{
		"no":   no,
		"name": "分页查询",
		"sex":  2,
	})
	defer addResp.Body.Close()
	requireHTTPStatus(t, addResp, http.StatusOK)

	resp := postJSON(t, "/student/page", map[string]any{
		"page":     1,
		"pageSize": 10,
		"no":       no,
	})
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusOK)

	var page response.Page[*studentsvc.StudentCO]
	decodeJSON(t, resp, &page)
	require.GreaterOrEqual(t, page.Total, int64(1))
	require.NotEmpty(t, page.Data)
	require.Equal(t, no, page.Data[0].No)
}

func TestStudentPage_Empty(t *testing.T) {
	resp := postJSON(t, "/student/page", map[string]any{
		"page":     1,
		"pageSize": 10,
		"no":       fmt.Sprintf("NONE%d", time.Now().UnixNano()),
	})
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusOK)

	var page response.Page[*studentsvc.StudentCO]
	decodeJSON(t, resp, &page)
	require.Zero(t, page.Total)
	require.Empty(t, page.Data)
}

func TestStudentPage_InvalidJSON(t *testing.T) {
	resp, err := http.Post(baseURL+"/student/page", "application/json", bytes.NewBufferString("{"))
	require.NoError(t, err)
	defer resp.Body.Close()
	requireHTTPStatus(t, resp, http.StatusBadRequest)
}
