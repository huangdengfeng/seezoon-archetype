package student

import (
	"context"

	"app-server/entity/utils"
	"app-server/logic"
)

type AddStudentCmdExe struct {
	studentService *logic.StudentService
}

func NewAddStudentCmdExe(studentService *logic.StudentService) *AddStudentCmdExe {
	return &AddStudentCmdExe{studentService: studentService}
}

type AddStudentCmd struct {
	No        string  `json:"no" validate:"required"`
	Name      string  `json:"name" validate:"required"`
	Sex       int8    `json:"sex" validate:"required,oneof=1 2"`
	Introduce *string `json:"introduce"`
	Birthday  *string `json:"birthday" validate:"omitempty,date"`
	Mobile    *string `json:"mobile" validate:"omitempty,max=45"`
}

type StudentCO struct {
	ID         int64   `json:"id"`
	No         string  `json:"no"`
	Name       string  `json:"name"`
	Sex        int8    `json:"sex"`
	Introduce  *string `json:"introduce"`
	Birthday   *string `json:"birthday"`
	Mobile     *string `json:"mobile"`
	Status     int8    `json:"status"`
	CreateTime string  `json:"createTime"`
	UpdateTime string  `json:"updateTime"`
}

func (e *AddStudentCmdExe) Execute(ctx context.Context, cmd *AddStudentCmd) (*StudentCO, error) {
	if err := utils.Validate(cmd); err != nil {
		return nil, err
	}

	birthday, err := utils.ParseDate(cmd.Birthday)
	if err != nil {
		return nil, err
	}

	info, err := e.studentService.AddStudent(ctx, &logic.AddStudentVO{
		No:        cmd.No,
		Name:      cmd.Name,
		Sex:       cmd.Sex,
		Introduce: cmd.Introduce,
		Birthday:  birthday,
		Mobile:    cmd.Mobile,
	})
	if err != nil {
		return nil, err
	}
	return &StudentCO{
		ID:         info.ID,
		No:         info.No,
		Name:       info.Name,
		Sex:        info.Sex,
		Introduce:  info.Introduce,
		Birthday:   utils.FormatDate(info.Birthday),
		Mobile:     info.Mobile,
		Status:     info.Status,
		CreateTime: utils.FormatDateTime(info.CreateTime),
		UpdateTime: utils.FormatDateTime(info.UpdateTime),
	}, nil
}
