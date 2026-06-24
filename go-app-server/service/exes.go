package service

import "app-server/service/student"

type Exes struct {
	AddStudentCmdExe  *student.AddStudentCmdExe
	StudentPageQryExe *student.StudentPageQryExe
}
