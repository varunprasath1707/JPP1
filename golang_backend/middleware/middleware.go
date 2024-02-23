package middleware

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"strings"

	"ZenitusJPP/constants"
	service "ZenitusJPP/service"
	utils "ZenitusJPP/utils"

	"github.com/felixge/httpsnoop"
	"github.com/google/uuid"
	"go.uber.org/zap"
)

type Middleware struct {
	AuthSrv service.Authentication
}

// HandlePanic captures the server crash and report is smoothly without
// affecting the other services.
func (m *Middleware) HandlePanic(next http.Handler) http.Handler {
	midd := func(resp http.ResponseWriter, r *http.Request) {
		defer func() {
			if msg := recover(); msg != nil {
				logger := utils.GetLogger(r.Context())
				logger.Panic("internal error", zap.Any("msg", msg))
				http.Error(resp, "500 - Internal Server Error", http.StatusInternalServerError)
			}
		}()

		next.ServeHTTP(resp, r)
	}

	return http.HandlerFunc(midd)
}

// AddRequestID middelware sets a unique request id for each request send.
func (m *Middleware) AddRequestID(next http.Handler) http.Handler {
	midd := func(rwt http.ResponseWriter, req *http.Request) {
		reqId := req.Header.Get("X-Request-ID")
		if reqId == "" {
			reqId = strings.ReplaceAll(uuid.NewString(), "-", "")
		}
		(rwt).Header().Set("Access-Control-Allow-Origin", "*")
		(rwt).Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		(rwt).Header().Set("Access-Control-Allow-Headers", "access-control-allow-origin, Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorisation, Authorization")
		(rwt).Header().Set("Content-Type", "application/json")
		(rwt).Header().Set("Access-Control-Expose-Headers", "Authorization")
		// get a new context and pass to the next middleware using it
		logger := utils.GetLogger(req.Context())
		logger = logger.With(zap.String("request_id", reqId))
		ctx := utils.SetLogger(req.Context(), logger)
		next.ServeHTTP(rwt, req.WithContext(ctx))
	}

	return http.HandlerFunc(midd)
}

// AuthenticateUser authenticates user jwt token with the user details in the database
// After successful authentication, user details are store in the request context.
func (m *Middleware) AuthenticateUser(next http.Handler) http.Handler {
	return http.HandlerFunc(func(resp http.ResponseWriter, req *http.Request) {
		fmt.Println("Test")
		if (*req).Method == "OPTIONS" {
			next.ServeHTTP(resp, req.WithContext(req.Context()))
		}
		authJWt := req.Header.Get(constants.Authorisation)
		if authJWt == "" {
			resp.Header().Set("Content-Type", "application/json; charset=utf-8")
			resp.WriteHeader(http.StatusUnauthorized)

			if err := json.NewEncoder(resp).Encode("Token not present"); err != nil {
				log.Printf("ERROR: renderJson - %q\n", err)
			}
			return
		}

		sessionUser, err := m.AuthSrv.ValidateToken(authJWt)
		if err != nil {
			resp.Header().Set("Content-Type", "application/json; charset=utf-8")
			resp.WriteHeader(http.StatusUnauthorized)

			if err := json.NewEncoder(resp).Encode("UnAuthorised"); err != nil {
				log.Printf("ERROR: renderJson - %q\n", err)
			}
			return
		}

		ctx := req.Context()
		ctx = utils.SetSessionUser(ctx, sessionUser)
		ctx = utils.SetAuthToken(ctx, authJWt)
		next.ServeHTTP(resp, req.WithContext(ctx))
	})
}

// AddLoggerToCtx will add the given logger to the request context and made available
// for next middlewares and controllers.
func (m *Middleware) AddLoggerToCtx(logger *zap.Logger) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(resp http.ResponseWriter, req *http.Request) {
			ctx := req.Context()
			ctx = utils.SetLogger(ctx, logger)
			req = req.WithContext(ctx)
			next.ServeHTTP(resp, req)
		})
	}
}

func (m *Middleware) CombinedLogA(next http.Handler) http.Handler {
	return http.HandlerFunc(func(resp http.ResponseWriter, req *http.Request) {
		ctx := req.Context()
		bodyBytes := make([]byte, req.ContentLength, req.ContentLength)
		if req.ContentLength > 0 {
			bodyBytes, _ = ioutil.ReadAll(req.Body)
			req.Body = ioutil.NopCloser(bytes.NewBuffer(bodyBytes))
		}

		metrics := httpsnoop.CaptureMetrics(next, resp, req.WithContext(ctx))

		// Get a new logger from context with some logger fields
		logger := utils.GetLogger(ctx)
		host, _, err := net.SplitHostPort(req.RemoteAddr)
		if err != nil {
			host = req.RemoteAddr
		}

		logger.Info("middleware combined log",
			zap.String("client_addr", host),
			zap.String("req_uri", req.Method+" "+req.RequestURI+" "+req.Proto),
			zap.String("req_body", string(bodyBytes)),
			zap.String("resp_status", http.StatusText(metrics.Code)),
			zap.Int("resp_code", metrics.Code),
			zap.Int64("resp_duration", metrics.Duration.Milliseconds()),
			zap.Int64("resp_body_bytes", metrics.Written),
			zap.String("referer", req.Referer()),
			zap.String("user_agent", req.UserAgent()),
		)
	})
}

func extractJWT(req *http.Request) string {
	raw := req.Header.Get("Authorization")
	pruned := strings.Replace(raw, "Bearer ", "", 1)
	return pruned
}
