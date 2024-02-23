package service

import (
	"context"
	"regexp"

	"ZenitusJPP/database/postgres"

	"github.com/pkg/errors"
	"go.uber.org/zap"
)

var SpecialCharRegexp *regexp.Regexp

func init() {
	var err error
	SpecialCharRegexp, err = regexp.Compile("[^a-zA-Z0-9]+")
	if err != nil {
		panic(err)
	}
}

var _ MasterUser = (*IMasterUser)(nil)

type MasterUser interface {
	CheckMasterUserApi(ctx context.Context) error
}

// IUser implements service.UsersService.
type IMasterUser struct {
	Logger          *zap.Logger
	PostgresQuerier postgres.Querier
}

func (a *IMasterUser) CheckMasterUserApi(ctx context.Context) error {
	err := errors.New("Success Services")
	return err
}
