package Database

// This file has been taken from https://github.com/soarcn/GoJsonSqlNullObj to support
// json marshal and unmarshal for the database/sql.NullTypes.
import (
	"bytes"
	"database/sql"
	"encoding/json"
)

var nullBytes = []byte("null")

func IsNull(src []byte) bool {
	return bytes.Equal(src, nullBytes)
}

type NullString struct {
	sql.NullString
}

/*
func (c NullString) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
	return append(buf, []byte(c.String)...), err
}

// DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// https://github.com/jackc/pgx/issues/967
func (c *NullString) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
	if IsTrueOrFalse(src) {
		return nil
	}

	c.String = string(src)
	c.Valid = len(c.String) > 0
	return nil
}

func (c NullString) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
	return c.EncodeText(ci, src)
}

func (c *NullString) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
	return c.DecodeText(ci, src)
}
*/

func (c NullString) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.String)
}

func (c *NullString) UnmarshalJSON(b []byte) error {
	if len(b) > 0 {
		c.Valid = true
	}

	return json.Unmarshal(b, &c.String)
}

// MarshalText implements encoding.TextMarshaler.
// It will encode a blank string when this String is null.
func (s NullString) MarshalText() ([]byte, error) {
	if !s.Valid {
		return []byte{}, nil
	}

	return []byte(s.String), nil
}

// UnmarshalText implements encoding.TextUnmarshaler.
// It will unmarshal to a null String if the input is a blank string.
func (s *NullString) UnmarshalText(text []byte) error {
	s.String = string(text)
	s.Valid = s.String != ""
	return nil
}

type NullFloat64 struct {
	sql.NullFloat64
}

/*
func (c NullFloat64) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
	str := strconv.FormatFloat(c.Float64, 'f', 4, 64)
	return append(buf, []byte(str)...), err
}

// DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// https://github.com/jackc/pgx/issues/967
func (c *NullFloat64) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
	// When src is either "true" or "false". Ommit it.
	// try to find the value is valid or not by parsing it.
	if IsTrueOrFalse(src) {
		return nil
	}
	tmp := strings.ReplaceAll(string(src), " ", "")
	if len(tmp) == 0 {
		return nil
	}

	c.Float64, err = strconv.ParseFloat(tmp, 64)
	if err != nil {
		return err
	}

	c.Valid = true
	return nil
}

func (c NullFloat64) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
	return c.EncodeText(ci, src)
}

func (c *NullFloat64) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
	return c.DecodeText(ci, src)
}
*/

func (c NullFloat64) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.Float64)
}

func (c *NullFloat64) UnmarshalJSON(b []byte) error {
	return json.Unmarshal(b, &c.Float64)
}

/*
// MarshalText implements encoding.TextMarshaler.
// It will encode a blank string when this String is null.
func (s NullFloat64) MarshalText() ([]byte, error) {
	float := strconv.FormatFloat(s.Float64, 'f', 4, 64)
	return []byte(float), nil
}

// UnmarshalText implements encoding.TextUnmarshaler.
// It will unmarshal to a null String if the input is a blank string.
func (s *NullFloat64) UnmarshalText(text []byte) (err error) {
	tmp := strings.ReplaceAll(string(text), " ", "")
	if len(tmp) == 0 {
		return nil
	}

	s.Float64, err = strconv.ParseFloat(tmp, 64)
	if err != nil {
		return err
	}

	s.Valid = true
	return nil
}
*/

type NullInt64 struct {
	sql.NullInt64
}

/*
func (c NullInt64) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
	str := strconv.FormatInt(c.Int64, 10)
	return append(buf, []byte(str)...), err
}

// DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// https://github.com/jackc/pgx/issues/967
func (c *NullInt64) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
	// When src is either "true" or "false". Ommit it.
	// try to find the value is valid or not by parsing it.
	if IsTrueOrFalse(src) {
		return nil
	}

	tmp := strings.ReplaceAll(string(src), " ", "")
	if len(tmp) == 0 {
		return nil
	}

	c.Int64, err = strconv.ParseInt(tmp, 10, 64)
	if err != nil {
		return err
	}

	c.Valid = true
	return nil
}

func (c NullInt64) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
	return c.EncodeText(ci, src)
}

func (c *NullInt64) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
	return c.DecodeText(ci, src)
}
*/

func (c NullInt64) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.Int64)
}

func (c *NullInt64) UnmarshalJSON(b []byte) error {
	return json.Unmarshal(b, &c.Int64)
}

/*
// MarshalText implements encoding.TextMarshaler.
// It will encode a blank string when this String is null.
func (s NullInt64) MarshalText() ([]byte, error) {
	float := strconv.FormatInt(s.Int64, 10)
	return []byte(float), nil
}

// UnmarshalText implements encoding.TextUnmarshaler.
// It will unmarshal to a null String if the input is a blank string.
func (s *NullInt64) UnmarshalText(text []byte) (err error) {
	tmp := strings.ReplaceAll(string(text), " ", "")
	if len(tmp) == 0 {
		s.Valid = false
		return nil
	}

	s.Int64, err = strconv.ParseInt(tmp, 10, 64)
	if err != nil {
		return err
	}

	s.Valid = true
	return nil
}
*/

type NullInt32 struct {
	sql.NullInt32
}

/*

func (c NullInt32) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
	str := strconv.FormatInt(int64(c.Int32), 10)
	return append(buf, []byte(str)...), err
}

// DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// https://github.com/jackc/pgx/issues/967
func (c *NullInt32) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
	// When src is either "true" or "false". Ommit it.
	// try to find the value is valid or not by parsing it.
	if IsTrueOrFalse(src) {
		return nil
	}

	tmpStr := strings.ReplaceAll(string(src), " ", "")
	if len(tmpStr) == 0 {
		return nil
	}

	tmp, err := strconv.ParseInt(tmpStr, 10, 64)
	if err != nil {
		return err
	}

	c.Int32 = int32(tmp)
	c.Valid = true
	return nil
}

func (c NullInt32) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
	return c.EncodeText(ci, src)
}

func (c *NullInt32) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
	return c.DecodeText(ci, src)
}
*/

func (c NullInt32) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.Int32)
}

func (c *NullInt32) UnmarshalJSON(b []byte) error {
	return json.Unmarshal(b, &c.Int32)
}

/*
// MarshalText implements encoding.TextMarshaler.
// It will encode a blank string when this String is null.
func (s NullInt32) MarshalText() ([]byte, error) {
	float := strconv.FormatInt(int64(s.Int32), 10)
	return []byte(float), nil
}

// UnmarshalText implements encoding.TextUnmarshaler.
// It will unmarshal to a null String if the input is a blank string.
func (s *NullInt32) UnmarshalText(text []byte) (err error) {
	tmp, err := strconv.ParseInt(string(text), 10, 32)
	if err != nil {
		return err
	}

	s.Int32 = int32(tmp)
	s.Valid = true
	return nil
}
*/

type NullBool struct {
	sql.NullBool
}

// func (c NullBool) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
// 	str := strconv.FormatBool(c.Bool)
// 	return append(buf, []byte(str)...), err
// }

// // DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// // https://github.com/jackc/pgx/issues/967
// func (c *NullBool) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
// 	// boolean value is already set
// 	if c.Bool {
// 		return nil
// 	}

// 	if len(src) != 0 {
// 		if tmp, err := strconv.ParseBool(string(src)); err != nil {
// 			c.Bool = tmp
// 			c.Valid = true
// 		}
// 	}

// 	return nil
// }

// func (c NullBool) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
// 	return c.EncodeText(ci, src)
// }

// func (c *NullBool) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
// 	return c.DecodeText(ci, src)
// }

func (c NullBool) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.Bool)
}

func (c *NullBool) UnmarshalJSON(b []byte) error {
	return json.Unmarshal(b, &c.Bool)
}

/*
// MarshalText implements encoding.TextMarshaler.
// It will encode a blank string when this String is null.
func (s NullBool) MarshalText() ([]byte, error) {
	tmp := strconv.FormatBool(s.Bool)
	return []byte(tmp), nil
}

// UnmarshalText implements encoding.TextUnmarshaler.
// It will unmarshal to a null String if the input is a blank string.
func (s *NullBool) UnmarshalText(text []byte) (err error) {
	tmp, err := strconv.ParseBool(string(text))
	if err != nil {
		return err
	}

	s.Bool = tmp
	s.Valid = true
	return nil
}
*/

type NullTime struct {
	sql.NullTime
}

func (c NullTime) MarshalJSON() ([]byte, error) {
	if c.Time.IsZero() {
		return json.Marshal("")
	}

	return json.Marshal(c.Time)
}

func (c *NullTime) UnmarshalJSON(b []byte) error {
	if c.Time.IsZero() {
		return nil
	}

	return json.Unmarshal(b, &c.Time)
}

// func (c NullTime) EncodeText(ci *pgtype.ConnInfo, buf []byte) (newBuf []byte, err error) {
// 	return append(buf, []byte(c.Time.String())...), err
// }

// // DecodeText is the custom decoder to resolve issues related to pgx utf8 encoding issue
// // https://github.com/jackc/pgx/issues/967
// func (c *NullTime) DecodeText(ci *pgtype.ConnInfo, src []byte) (err error) {
// 	// When src is either "true" or "false". Ommit it.
// 	// try to find the value is valid or not by parsing it.
// 	if IsTrueOrFalse(src) {
// 		return nil
// 	}

// 	if len(src) == 0 {
// 		return nil
// 	}

// 	timeFormats := []string{time.RFC1123, time.RFC1123Z, time.RFC3339, time.RFC3339Nano, time.RFC822, time.RFC822, time.RFC822Z, time.RFC850, time.ANSIC, time.Layout, time.Stamp, time.StampMicro, time.StampMilli, time.StampNano, time.Kitchen, ""}

// 	for i := range timeFormats {
// 		c.Time, err = time.Parse(string(src), timeFormats[i])
// 		if err != nil {
// 			log.Println("timeLayout - not : ", timeFormats[i])
// 			continue
// 		}

// 		log.Println("timeLayout: ", timeFormats[i])
// 		break
// 	}

// 	if err != nil {
// 		return err
// 	}

// 	c.Valid = true
// 	return nil
// }

// func (c NullTime) EncodeBinary(ci *pgtype.ConnInfo, src []byte) (newBuf []byte, err error) {
// 	return c.EncodeText(ci, src)
// }

// func (c *NullTime) DecodeBinary(ci *pgtype.ConnInfo, src []byte) error {
// 	return c.DecodeText(ci, src)
// }

// When src is either "true" or "false". Ommit it.
// try to find the value is valid or not by parsing it.
func IsTrueOrFalse(src []byte) bool {
	return string(src) == "true" || string(src) == "false"
}
