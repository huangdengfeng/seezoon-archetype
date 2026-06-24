package logic

import (
	"context"
	"time"

	"app-server/entity/errorx"
	"app-server/entity/utils"
	"app-server/repo/dao"
)

type StudentService struct {
	studentInfoDao *dao.StudentInfoDao
}

func NewStudentService(studentInfoDao *dao.StudentInfoDao) *StudentService {
	return &StudentService{studentInfoDao: studentInfoDao}
}

type AddStudentVO struct {
	No        string     `validate:"required"`           // 学号 not null
	Name      string     `validate:"required"`           // 姓名 not null
	Sex       int8       `validate:"required,oneof=1 2"` // 性别：StudentSexMale、StudentSexFemale not null
	Introduce *string    // 介绍
	Birthday  *time.Time // 生日
	Mobile    *string    // 手机号
}

type StudentInfo struct {
	ID         int64
	No         string
	Name       string
	Sex        int8
	Introduce  *string
	Birthday   *time.Time
	Mobile     *string
	Status     int8
	CreateTime time.Time
	UpdateTime time.Time
}

func (s *StudentService) AddStudent(ctx context.Context, vo *AddStudentVO) (*StudentInfo, error) {
	if err := utils.Validate(vo); err != nil {
		return nil, err
	}

	var info *dao.StudentInfoPO
	err := dao.DoTransaction(ctx, func(txCtx context.Context) error {
		existing, err := s.studentInfoDao.SelectByNo(txCtx, vo.No)
		if err != nil {
			return err
		}
		if existing != nil {
			return errorx.With(errorx.BadArgs, "no already exists")
		}

		now := time.Now()
		info = &dao.StudentInfoPO{
			No:         vo.No,
			Name:       vo.Name,
			Sex:        vo.Sex,
			Introduce:  vo.Introduce,
			Birthday:   vo.Birthday,
			Mobile:     vo.Mobile,
			Status:     dao.DbRecordStatusValid,
			CreateTime: now,
			UpdateTime: now,
		}
		_, err = s.studentInfoDao.Insert(txCtx, info)
		return err
	})
	if err != nil {
		return nil, err
	}
	return toStudentInfo(info), nil
}

func toStudentInfo(po *dao.StudentInfoPO) *StudentInfo {
	return &StudentInfo{
		ID:         po.ID,
		No:         po.No,
		Name:       po.Name,
		Sex:        po.Sex,
		Introduce:  po.Introduce,
		Birthday:   po.Birthday,
		Mobile:     po.Mobile,
		Status:     po.Status,
		CreateTime: po.CreateTime,
		UpdateTime: po.UpdateTime,
	}
}
