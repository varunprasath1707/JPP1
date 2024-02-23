package postgres

import (
	"context"
	"fmt"
	"log"
	"strconv"
	"time"

	"ZenitusJPP/configuration"
	"ZenitusJPP/constants"
	"ZenitusJPP/database/postgres/gen"

	_ "github.com/golang-migrate/migrate/v4/source/file"
	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
	_ "github.com/lib/pq"
)

var conn *pgxpool.Pool

type DBTX interface {
	gen.DBTX
	CopyFrom(ctx context.Context, tableName pgx.Identifier, columnNames []string, rowSrc pgx.CopyFromSource) (int64, error)
	SendBatch(ctx context.Context, b *pgx.Batch) pgx.BatchResults
}

type Querier interface {
	DBTX
	CustomQuerier
	gen.UserQuerier
}

type Queries struct {
	DBTX
	*gen.Queries
}

type CustomQuerier interface {
}

var _ CustomQuerier = (*Queries)(nil)

func Connect(cfg configuration.Config) *Queries {
	port, err := strconv.Atoi(cfg.GetString(constants.DBPort))
	if err != nil {
		log.Fatal(err.Error())
	}
	connectionString := fmt.Sprintf(
		"host=%s port=%d user=%s password=%s dbname=%s sslmode=disable",
		cfg.GetString(constants.DBHost),
		port,
		cfg.GetString(constants.DBUserName),
		cfg.GetString(constants.DBPassword),
		cfg.GetString(constants.DBName))

	config, err := pgxpool.ParseConfig(connectionString)
	if err != nil {
		log.Fatal(err.Error())
	}

	config.HealthCheckPeriod = 6 * time.Minute
	config.MaxConnLifetime = 4 * time.Minute
	config.MaxConnIdleTime = 2 * time.Minute
	conn, err = pgxpool.ConnectConfig(context.Background(), config)
	if err != nil {
		log.Fatalf(err.Error())
	}

	return &Queries{
		DBTX:    conn,
		Queries: gen.New(conn),
	}
}

type Tx struct {
	Queries
	pgx.Tx
}

func BeginTx(ctx context.Context, options *pgx.TxOptions) (*Tx, error) {
	var (
		txn pgx.Tx
		err error
	)

	if options == nil {
		txn, err = conn.Begin(ctx)
	} else {
		txn, err = conn.BeginTx(ctx, *options)
	}

	if err != nil {
		return nil, fmt.Errorf("postgres.BeginTx: %w", err)
	}

	return &Tx{
		Tx: txn,
		Queries: Queries{
			DBTX:    txn,
			Queries: gen.New(conn),
		},
	}, nil
}
