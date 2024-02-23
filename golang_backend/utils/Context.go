package utils

import (
	"ZenitusJPP/database/postgres/gen"
	"context"

	"go.uber.org/zap"
)

// Request Context Values.
const (
	SessionUserCtx string = "sessionUser"
	AuthTokenCtx   string = "token"
	LoggerCtx      string = "logger"
)

func GetAuthToken(ctx context.Context) string {
	if token, ok := ctx.Value(AuthTokenCtx).(string); ok {
		return token
	}

	return ""
}

func SetAuthToken(ctx context.Context, token string) context.Context {
	return context.WithValue(ctx, AuthTokenCtx, token)
}

func GetSessionUser(ctx context.Context) *gen.SelectUserIDByTokenRow {
	if sessionUser, ok := ctx.Value(SessionUserCtx).(*gen.SelectUserIDByTokenRow); ok {
		return sessionUser
	}

	return nil
}

func SetSessionUser(ctx context.Context, sessionUser *gen.SelectUserIDByTokenRow) context.Context {
	return context.WithValue(ctx, SessionUserCtx, sessionUser)
}

func GetLogger(ctx context.Context) *zap.Logger {
	logger, ok := ctx.Value(LoggerCtx).(*zap.Logger)
	if !ok {
		return nil
	}

	return logger
}

func SetLogger(ctx context.Context, logger *zap.Logger) context.Context {
	return context.WithValue(ctx, LoggerCtx, logger)
}
