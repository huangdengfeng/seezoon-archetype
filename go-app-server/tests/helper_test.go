package tests

import (
	"bytes"
	"encoding/json"
	"io"
	"net/http"
	"testing"

	"github.com/stretchr/testify/require"
)

const baseURL = "http://127.0.0.1:8080"

func postJSON(t *testing.T, path string, body any) *http.Response {
	t.Helper()
	b, err := json.Marshal(body)
	require.NoError(t, err)
	resp, err := http.Post(baseURL+path, "application/json", bytes.NewReader(b))
	require.NoError(t, err)
	return resp
}

func requireHTTPStatus(t *testing.T, resp *http.Response, want int) {
	t.Helper()
	if resp.StatusCode != want {
		b, _ := io.ReadAll(resp.Body)
		require.FailNowf(t, "unexpected status", "want=%d got=%d body=%s", want, resp.StatusCode, b)
	}
}

func decodeJSON[T any](t *testing.T, resp *http.Response, v *T) {
	t.Helper()
	require.NoError(t, json.NewDecoder(resp.Body).Decode(v))
}
