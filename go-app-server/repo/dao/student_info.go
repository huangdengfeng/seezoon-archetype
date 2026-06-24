package dao

import (
	"context"
	"errors"
	"log/slog"
	"time"

	"app-server/entity/errorx"

	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

const (
	StudentSexMale   int8 = 1 // 男
	StudentSexFemale int8 = 2 // 女
)

type StudentInfoPO struct {
	ID         int64      `gorm:"column:id;primaryKey;autoIncrement"` // 学生ID not null
	No         string     `gorm:"column:no"`                          // 学号 not null
	Name       string     `gorm:"column:name"`                        // 姓名 not null
	Sex        int8       `gorm:"column:sex"`                         // 性别：StudentSexMale、StudentSexFemale not null
	Introduce  *string    `gorm:"column:introduce"`                   // 介绍
	Birthday   *time.Time `gorm:"column:birthday"`                    // 生日
	Mobile     *string    `gorm:"column:mobile"`                      // 手机号
	Status     int8       `gorm:"column:status"`                      // 状态：DbRecordStatusValid、DbRecordStatusInvalid not null
	CreateTime time.Time  `gorm:"column:create_time"`                 // 创建时间 not null
	UpdateTime time.Time  `gorm:"column:update_time"`                 // 更新时间 not null
}

func (StudentInfoPO) TableName() string {
	return "student_info"
}

type StudentInfoCondition struct {
	Page     int
	PageSize int
	No       string
	Name     string
	Mobile   string
	Status   *int8
}

type StudentInfoDao struct{}

func NewStudentInfoDao() *StudentInfoDao {
	return &StudentInfoDao{}
}

func (d *StudentInfoDao) Insert(ctx context.Context, info *StudentInfoPO) (int64, error) {
	result := DB(ctx).Create(info)
	if result.Error != nil {
		slog.Error("student_info insert failed", "err", result.Error)
		return 0, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, result.Error)
	}
	if result.RowsAffected != 1 {
		slog.Error("student_info insert failed", "rows", result.RowsAffected)
		return 0, errorx.New(errorx.RowsAffectedNotMatch.Code, errorx.RowsAffectedNotMatch.Msg, result.RowsAffected)
	}
	return info.ID, nil
}

func (d *StudentInfoDao) Update(ctx context.Context, info *StudentInfoPO) (int64, error) {
	if info.ID == 0 {
		slog.Error("student_info update failed", "reason", "id is required")
		return 0, errorx.New(errorx.BadArgs.Code, errorx.BadArgs.Msg, "id is required")
	}
	result := DB(ctx).Save(info)
	if result.Error != nil {
		slog.Error("student_info update failed", "err", result.Error)
		return 0, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, result.Error)
	}
	if result.RowsAffected != 1 {
		slog.Error("student_info update failed", "rows", result.RowsAffected)
		return 0, errorx.New(errorx.RowsAffectedNotMatch.Code, errorx.RowsAffectedNotMatch.Msg, result.RowsAffected)
	}
	return result.RowsAffected, nil
}

func (d *StudentInfoDao) Delete(ctx context.Context, id int64) (int64, error) {
	result := DB(ctx).Delete(&StudentInfoPO{}, id)
	if result.Error != nil {
		slog.Error("student_info delete failed", "err", result.Error)
		return 0, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, result.Error)
	}
	if result.RowsAffected != 1 {
		slog.Error("student_info delete failed", "rows", result.RowsAffected)
		return 0, errorx.New(errorx.RowsAffectedNotMatch.Code, errorx.RowsAffectedNotMatch.Msg, result.RowsAffected)
	}
	return result.RowsAffected, nil
}

func (d *StudentInfoDao) SelectByID(ctx context.Context, id int64) (*StudentInfoPO, error) {
	var info StudentInfoPO
	if err := DB(ctx).First(&info, id).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, nil
		}
		slog.Error("student_info select by id failed", "id", id, "err", err)
		return nil, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, err)
	}
	return &info, nil
}

func (d *StudentInfoDao) SelectByIDForUpdate(ctx context.Context, id int64) (*StudentInfoPO, error) {
	var info StudentInfoPO
	if err := DB(ctx).Clauses(clause.Locking{Strength: "UPDATE"}).First(&info, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		slog.Error("student_info select by id for update failed", "id", id, "err", err)
		return nil, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, err)
	}
	return &info, nil
}

func (d *StudentInfoDao) SelectByNo(ctx context.Context, no string) (*StudentInfoPO, error) {
	var info StudentInfoPO
	if err := DB(ctx).Where("no = ?", no).First(&info).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		slog.Error("student_info select by no failed", "no", no, "err", err)
		return nil, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, err)
	}
	return &info, nil
}

func (d *StudentInfoDao) Page(ctx context.Context, q *StudentInfoCondition) (int64, []*StudentInfoPO, error) {
	query := DB(ctx).Model(&StudentInfoPO{})
	if q != nil {
		if q.No != "" {
			query = query.Where("no = ?", q.No)
		}
		if q.Name != "" {
			query = query.Where("name LIKE ?", "%"+q.Name+"%")
		}
		if q.Mobile != "" {
			query = query.Where("mobile = ?", q.Mobile)
		}
		if q.Status != nil {
			query = query.Where("status = ?", *q.Status)
		}
	}

	var total int64
	if err := query.Count(&total).Error; err != nil {
		slog.Error("student_info page count failed", "err", err)
		return 0, nil, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, err)
	}
	if total == 0 {
		return 0, []*StudentInfoPO{}, nil
	}

	list := make([]*StudentInfoPO, 0)
	if err := query.Order("id DESC").Offset((q.Page - 1) * q.PageSize).Limit(q.PageSize).Find(&list).Error; err != nil {
		slog.Error("student_info page query failed", "err", err)
		return 0, nil, errorx.New(errorx.SqlError.Code, errorx.SqlError.Msg, err)
	}
	return total, list, nil
}
