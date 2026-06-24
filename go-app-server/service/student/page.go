package student

import (
	"app-server/entity/response"
	"app-server/entity/utils"
	"context"

	"app-server/repo/dao"
)

type StudentPageQryExe struct {
	studentInfoDao *dao.StudentInfoDao
}

func NewStudentPageQryExe(studentInfoDao *dao.StudentInfoDao) *StudentPageQryExe {
	return &StudentPageQryExe{studentInfoDao: studentInfoDao}
}

type StudentPageQry struct {
	Page     int    `json:"page"`
	PageSize int    `json:"pageSize"`
	No       string `json:"no"`
	Name     string `json:"name"`
	Mobile   string `json:"mobile"`
	Status   *int8  `json:"status"`
}

func (e *StudentPageQryExe) Execute(ctx context.Context, qry *StudentPageQry) (response.Page[*StudentCO], error) {
	total, list, err := e.studentInfoDao.Page(ctx, &dao.StudentInfoCondition{
		Page:     qry.Page,
		PageSize: qry.PageSize,
		No:       qry.No,
		Name:     qry.Name,
		Mobile:   qry.Mobile,
		Status:   qry.Status,
	})
	if err != nil {
		return response.Page[*StudentCO]{}, err
	}

	data := make([]*StudentCO, 0, len(list))
	for _, po := range list {
		data = append(data, &StudentCO{
			ID:         po.ID,
			No:         po.No,
			Name:       po.Name,
			Sex:        po.Sex,
			Introduce:  po.Introduce,
			Birthday:   utils.FormatDate(po.Birthday),
			Mobile:     po.Mobile,
			Status:     po.Status,
			CreateTime: utils.FormatDateTime(po.CreateTime),
			UpdateTime: utils.FormatDateTime(po.UpdateTime),
		})
	}
	return response.Page[*StudentCO]{
		Total: total,
		Data:  data,
	}, nil
}
