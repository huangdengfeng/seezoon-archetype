package response

import (
	"net/http"

	"app-server/entity/errorx"

	"github.com/gin-gonic/gin"
)

const ProblemJSON = "application/problem+json"

type Problem struct {
	Type     string `json:"type"`
	Title    string `json:"title"`
	Status   int    `json:"status"`
	Detail   string `json:"detail,omitempty"`
	Instance string `json:"instance,omitempty"`
	Code     int    `json:"code,omitempty"`
}

type Page[T any] struct {
	Total int64 `json:"total"`
	Data  []T   `json:"data"`
}

func WriteProblem(c *gin.Context, status, code int, title, detail string) {
	c.Header("Content-Type", ProblemJSON)
	c.JSON(status, Problem{
		Type:     "about:blank",
		Title:    title,
		Status:   status,
		Detail:   detail,
		Instance: c.Request.URL.Path,
		Code:     code,
	})
}

func WriteError(c *gin.Context, err error) {
	if e, ok := errorx.As(err); ok {
		WriteProblem(c, http.StatusBadRequest, e.Code, http.StatusText(http.StatusBadRequest), e.Msg)
		return
	}
	WriteProblem(c, http.StatusInternalServerError, errorx.Unknown.Code, "Internal Server Error", err.Error())
}
