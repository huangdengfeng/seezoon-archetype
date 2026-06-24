package service

import (
	"net/http"

	"app-server/entity/errorx"
	"app-server/entity/response"
	studentsvc "app-server/service/student"

	"github.com/gin-gonic/gin"
)

func NewRouter(exes Exes) *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	r := gin.New()
	r.Use(gin.Recovery())

	r.POST("/student/add", func(c *gin.Context) {
		var cmd studentsvc.AddStudentCmd
		if err := c.ShouldBindJSON(&cmd); err != nil {
			response.WriteProblem(c, http.StatusBadRequest, errorx.BadArgs.Code, "Bad Request", err.Error())
			return
		}
		co, err := exes.AddStudentCmdExe.Execute(c.Request.Context(), &cmd)
		if err != nil {
			response.WriteError(c, err)
			return
		}
		c.JSON(http.StatusCreated, co)
	})

	r.POST("/student/page", func(c *gin.Context) {
		var qry studentsvc.StudentPageQry
		if err := c.ShouldBindJSON(&qry); err != nil {
			response.WriteProblem(c, http.StatusBadRequest, errorx.BadArgs.Code, "Bad Request", err.Error())
			return
		}
		page, err := exes.StudentPageQryExe.Execute(c.Request.Context(), &qry)
		if err != nil {
			response.WriteError(c, err)
			return
		}
		c.JSON(http.StatusOK, page)
	})

	return r
}
