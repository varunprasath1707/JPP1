package configuration

import (
	"fmt"
	"log"
	"os"

	"github.com/spf13/viper"
	"go.uber.org/zap"
)

type Config interface {
	GetString(key string) string
	Get(key string) interface{}
	GetInt(key string) int
	GetStringArr(key string) []string
	Init(filePath, schemaPath string) (*viperConfig, error)
	GetOsEnvString(key string) string
	// Logger() *zap.Logger
	Close()
}

type viperConfig struct {
	logger *zap.Logger
}

// Logger implements Config.
func (*viperConfig) Logger() *zap.Logger {
	panic("unimplemented")
}

func (v *viperConfig) Init(filePath, schemaPath string) (*viperConfig, error) {
	viper.SetConfigFile(filePath)

	if err := viper.ReadInConfig(); err != nil {
		log.Printf("failed to read configuration from: %v, err: %+v", filePath, err)

		return nil, fmt.Errorf("failed to read configuration: %w", err)
	}

	log.Printf("successfully read configuration from: %v", filePath)

	return v, nil
}

func (v *viperConfig) GetString(key string) string {
	return viper.GetString(key)
}

func (v *viperConfig) GetInt(key string) int {
	return viper.GetInt(key)
}

func (v *viperConfig) GetStringArr(key string) []string {
	return viper.GetStringSlice(key)
}

func (v *viperConfig) Get(key string) interface{} {
	return viper.Get(key)
}

func (v *viperConfig) GetOsEnvString(key string) string {
	return os.Getenv(key)
}

func NewViperConfig() *viperConfig {
	return &viperConfig{}
}

func (v *viperConfig) Close() {
	if err := v.logger.Sync(); err != nil {
		log.Println(err)
	}
}
