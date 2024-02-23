package service

import (
	"ZenitusJPP/configuration"
	"ZenitusJPP/database/postgres"
	"ZenitusJPP/database/postgres/gen"
	"ZenitusJPP/models"
	"bytes"
	"context"
	"database/sql"
	"encoding/csv"
	"encoding/json"
	"fmt"
	"log"
	"math/rand"
	"net/http"
	"net/smtp"
	"regexp"
	"strconv"
	"strings"
	"time"

	"github.com/dgrijalva/jwt-go"
	"github.com/pkg/errors"
)

var _ Authentication = (*IAuthentication)(nil)

type Authentication interface {
	GenerateJWT(user models.JWTUserDetails, postgresQuerier postgres.Querier) (string, error)
	ValidateToken(token string) (*gen.SelectUserIDByTokenRow, error)
}

type IAuthentication struct {
	cfg             configuration.Config
	PostgresQuerier postgres.Querier
}

const (
	SessionUserCtx string = "sessionUser"
	AuthTokenCtx   string = "token"
)

func (l IAuthentication) LogOut(token string) error {
	return nil
}

func (l IAuthentication) JWTToken(ReqObj models.JWTUserDetails, processtype string) (string, error) {
	jwtToken, err := l.GenerateJWT(ReqObj, l.PostgresQuerier)
	return jwtToken, err
}

func (l IAuthentication) ValidateToken(token string) (*gen.SelectUserIDByTokenRow, error) {
	// decodedToken, err := jwt.ParseWithClaims(token, &models.JwtClaim{}, func(token *jwt.Token) (interface{}, error) {
	// 	return []byte(l.cfg.GetString(constants.JWTSecret)), nil
	// },
	// )
	// if err != nil && err.Error() != "Token used before issued" {
	// 	log.Printf("error: ValidateToken - %s", err.Error())
	// 	return nil, err
	// }

	// claim, ok := decodedToken.Claims.(*models.JwtClaim)
	// if !ok {
	// 	return nil, errors.New("couldn't parse claims")
	// }

	// if claim.Issuer != l.cfg.GetString(constants.JWTIssuer) {
	// 	return nil, errors.New("invalid issuer")
	// }

	sessionUser, err := l.PostgresQuerier.SelectUserIDByToken(context.Background(), token)
	if err != nil {
		log.Printf("error: ValidateToken (sessionUser - get) %s", err.Error())
		return nil, errors.New("unauthorized session")
	}

	return sessionUser, nil
}

func (l IAuthentication) GenerateJWT(user models.JWTUserDetails, queries postgres.Querier) (string, error) {
	claims := new(models.JwtClaim)
	claims.Identifier = user.ID
	claims.UserID = user.UserID
	claims.Username = user.Name
	claims.Email = user.Email
	claims.SetIssuer("jwt_issuer")
	claims.IssuedAt = time.Now().UnixNano()
	tokenIns := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	token, err := tokenIns.SignedString([]byte("jwt_secret"))
	if err != nil {
		log.Printf("error: GenerateToken (SignedString) - %s", err.Error())
		return "", nil
	}
	return token, nil
}

func NewLogin(querier postgres.Querier, cfg configuration.Config) Authentication {
	return &IAuthentication{
		cfg:             cfg,
		PostgresQuerier: querier,
	}
}

func TxRollBack(tx *sql.Tx) {
	if err := tx.Rollback(); err != nil {
		log.Printf("error: TxRollBack - %s", err.Error())
	}
}

func TxCommit(tx *sql.Tx) {
	if err := tx.Commit(); err != nil {
		log.Printf("error: TxCommit - %s", err.Error())
	}
}

func WriteResponse(resp http.ResponseWriter, obj models.ResponseObj) {
	resp.Header().Set("Access-Control-Allow-Origin", "*")
	resp.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
	resp.Header().Set("Access-Control-Allow-Headers", "access-control-allow-origin, Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorisation, Authorization")
	resp.Header().Set("Content-Type", "application/octet-stream")
	resp.Header().Set("Access-Control-Expose-Headers", "Authorization")
	resp.WriteHeader(http.StatusOK)
	resp.Header().Set("Content-Type", "application/json")
	resp.Header().Set("Server", "CIC")
	err := json.NewEncoder(resp).Encode(obj)
	if err != nil {
		fmt.Println(err)
		return
	}
	log.Printf("[SUCCESS] AdminUsersController.CheckAdminApi():\n")
}

func WriteResponse2(resp http.ResponseWriter, obj models.ResponseObj2) {
	resp.Header().Set("Access-Control-Allow-Origin", "*")
	resp.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
	resp.Header().Set("Access-Control-Allow-Headers", "access-control-allow-origin, Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorisation, Authorization")
	resp.Header().Set("Content-Type", "application/octet-stream")
	resp.Header().Set("Access-Control-Expose-Headers", "Authorization")
	resp.WriteHeader(http.StatusOK)
	resp.Header().Set("Content-Type", "application/json")
	resp.Header().Set("Server", "CIC")
	err := json.NewEncoder(resp).Encode(obj)
	if err != nil {
		fmt.Println(err)
		return
	}
	log.Printf("[SUCCESS] AdminUsersController.CheckAdminApi():\n")
}

func Notification(Not_type string) (err error) {
	var notiobj gen.NotificationInsertParams
	if Not_type == "Create-Agenda" {
		notiobj.UserID = 1
		notiobj.NotificationText, err = NotiStringAgendaCreate()
	}
	return
}

func NotiStringAgendaCreate() (not_string string, err error) {
	not_string = fmt.Sprintf("a new agenda has been added by %s on %s which says %s", 'a', 'a', 'a')
	return
}

func CheckBlankParameter(para string) (haserror bool) {
	haserror = false
	para = strings.ReplaceAll(para, " ", "")
	if para == "" {
		haserror = true
	}
	return
}

func WriteResponseErr(resp http.ResponseWriter, statusCode int, err error) {
	resp.WriteHeader(statusCode)
	err = json.NewEncoder(resp).Encode(models.Response{
		Data: models.ResponseObj{
			Message:      err.Error(),
			HasError:     true,
			ErrorMessage: err.Error(),
		},
	})

	if err != nil {
		log.Println("unable to write response writeError():", err.Error())
	}
}

func WriteResponseErrjapan(resp http.ResponseWriter, statusCode int, err error, japan string) {
	resp.WriteHeader(statusCode)
	err = json.NewEncoder(resp).Encode(models.Response{
		Data: models.ResponseObj{
			Message:         err.Error(),
			JapaneseMessage: japan,
			HasError:        true,
			ErrorMessage:    err.Error(),
		},
	})

	if err != nil {
		log.Println("unable to write response writeError():", err.Error())
	}
}

func GenerateOTPCode() string {
	var pool = "123456789"
	l := 4
	rand.Seed(time.Now().UnixNano())
	bytes := make([]byte, l)

	for i := 0; i < l; i++ {
		bytes[i] = pool[rand.Intn(len(pool))]
	}

	return string(bytes)
}

func SendEmail(EmailID, otp, name string, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := []string{EmailID}
	subject := "JPP Registration OTP Verification"
	from := dynamic.Values
	body := fmt.Sprintf("名前：%s\n\n"+
		"当社サイトへの電子メール登録用のワンタイム パスワード (OTP) は %s です。\n"+
		"このパスワードは 60 分間有効です。 有効期限が切れたら、再度ご登録ください。\n"+
		"これはシステムが生成したメールです。", name, otp)

	// Compose the email
	msg := fmt.Sprintf("To: %s\r\nFrom:%s\r\nSubject: %s\r\n\r\n%s", to[0], from, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, to, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")
	return err
}

func ReSendEmailOTP(EmailID, otp, name string, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := EmailID
	subject := "JPP Forgot Password OTP verification"
	from := dynamic.Values
	body := fmt.Sprintf("名前：%s\n\n"+
		"当社サイトへの電子メール登録用のワンタイム パスワード (OTP) は %s です。\n"+
		"このパスワードは 60 分間有効です。 有効期限が切れたら、再度ご登録ください。\n"+
		"これはシステムが生成したメールです。", name, otp)

	// Compose the email
	msg := fmt.Sprintf("From: %s\r\nTo:%s\r\nSubject: %s\r\n\r\n%s", from, to, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, []string{to}, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")
	return err
}

func ReSendEmailForMobileOtp(EmailID, otp, name string, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := EmailID
	subject := "JPP Registration OTP Code for Mobile"
	from := dynamic.Values
	body := fmt.Sprintf("名前：%s\n\n"+
		"当社サイトへの電子メール登録用のワンタイム パスワード (OTP) は %s です。\n"+
		"このパスワードは 60 分間有効です。 有効期限が切れたら、再度ご登録ください。\n"+
		"これはシステムが生成したメールです。", name, otp)

	// Compose the email
	msg := fmt.Sprintf("From: %s\r\nTo:%s\r\nSubject: %s\r\n\r\n%s", from, to, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, []string{to}, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")
	return err
}

func IsValidEmail(email string) bool {
	// Define a simple regex for email validation
	emailRegex := regexp.MustCompile(`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`)
	return emailRegex.MatchString(email)
}

func IsValidPassword(password string) bool {
	// Password should be at least 8 characters long, contain at least one uppercase letter,
	// at least one numeric digit, and at least one special character.
	hasUpperCase := false
	hasDigit := false
	hasSpecialChar := false

	for _, char := range password {
		switch {
		case 'A' <= char && char <= 'Z':
			hasUpperCase = true
		case '0' <= char && char <= '9':
			hasDigit = true
		case char == '!' || char == '@' || char == '#' || char == '$' || char == '%' || char == '^' || char == '&' || char == '*' || char == '(' || char == ')' || char == '-' || char == '_' || char == '+':
			hasSpecialChar = true
		}
	}

	return len(password) >= 8 && hasUpperCase && hasDigit && hasSpecialChar
}
func IsValidMobileNumber(number string) bool {
	// Define a regular expression pattern for a 10-digit mobile number
	// Adjust the pattern based on your specific requirements and country code.
	pattern := `^\d{10}$`

	// Compile the regular expression
	regex := regexp.MustCompile(pattern)

	// Use the regular expression to match the mobile number
	return regex.MatchString(number)
}

func EmailUserMembershipInfo(userobj models.UserResponse, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) (err error) {
	// Extracting Fullname
	Fullname := userobj.FirstName + " " + userobj.LastName

	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := userobj.EmailID
	subject := "Membership Renewal Reminder"
	from := dynamic.Values
	body := "親愛なる " + Fullname + " さんへ、\n\n" +
		"お元気でいらっしゃいますか。このメッセージがあなたに届きますことを願っております。JPP とのメンバーシップを通じて、あなたのご支援に心より感謝申し上げます。\n" +
		"尊敬する会員として、あなたの会員資格が５日後に満了しますことをお知らせいたします。私たちのサービスと特典のすべてを引き続きお楽しみいただくために、会員資格の更新をお勧めいたします。\n\n" +
		"会員資格の更新は簡単です。当社のウェブサイトにログインし、更新手順に従ってください。\n\n" +
		"ご質問やお手伝いが必要な場合は、お気軽にお問い合わせください。お手伝いさせていただきます。\n\n" +
		"JPP コミュニティの一員でいてくださって、誠にありがとうございます。\n\n" +
		"敬具、\n" +
		"JPP 会員開発チーム"

	// Compose the email
	msg := fmt.Sprintf("From: %s\r\nTo:%s\r\nSubject: %s\r\n\r\n%s", from, to, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err = smtp.SendMail(add, auth, from, []string{to}, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")
	return err
}

func SendPetitionListEmail(EmailID, comment string, petitions []gen.SelectAllUseracceptenceByPetitionIDRow, petitionInfo []gen.SelectPetitionInfoByPetitionIdRow, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum, dynamic4 *gen.DynamicDatum) error {
	// Check if the petitions slice is empty
	if len(petitions) == 0 {
		fmt.Println("Petitions slice is empty.")
		return nil
	}

	// Check if the petitionInfo slice is empty
	if len(petitionInfo) == 0 {
		fmt.Println("PetitionInfo slice is empty.")
		return nil
	}
	fmt.Println("----------------------------------------------------------------")
	fmt.Println("Title :" + petitionInfo[0].Title)
	fmt.Println("Name :" + petitionInfo[0].HandleName)
	fmt.Println("Content :" + petitionInfo[0].PetitionContent)
	fmt.Println("Address :" + petitionInfo[0].SubmissionAddress)
	fmt.Println("Deadline :" + petitionInfo[0].Deadline.Format("2006-01-02 15:04:05"))
	fmt.Println("comments :" + petitionInfo[0].Recruitmentcomments)

	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and CC details
	to := dynamic4.Values // Send email to info@chokumin.com
	cc := EmailID

	// Email subject
	subject := "[署名の提出依頼があります]　－　 : " + petitionInfo[0].Title

	// Body content
	body :=
		"署名 : " + petitionInfo[0].Title + "\r\n" +
			"対象 : " + petitionInfo[0].HandleName + "\r\n" +
			"提出先 : " + strings.ReplaceAll(petitionInfo[0].PetitionContent, "<br/>", "\n") + "\r\n" +
			"提出先の住所 : " + petitionInfo[0].SubmissionAddress + "\r\n" +
			"募集期限 : " + petitionInfo[0].Deadline.Format("2006-01-02 15:04:05") + "\r\n" +
			"募集コメント : " + strings.ReplaceAll(petitionInfo[0].Recruitmentcomments, "<br/>", "\n") + "\r\n\n" +
			"管理者への追加連絡事項 :" + comment + "\r\n\n" +
			"[署名者の一覧をCSVファイルとして添付します」"

	// Create a buffer to write CSV data
	var csvBuffer bytes.Buffer
	writer := csv.NewWriter(&csvBuffer)

	// Write CSV header
	header := []string{"S.No", "氏名", "住所", "日時"} // Add more fields as needed
	writer.Write(header)

	// Write petition data to CSV with an incremental serial number
	for i, petition := range petitions {
		fullName := petition.FirstName + " " + petition.LastName
		row := []string{
			strconv.Itoa(i + 1),
			fullName,
			petition.UserAddress,
			petition.CreatedOn.Format("2006-01-02 15:04:05"), // Add more fields as needed
		}
		writer.Write(row)
	}

	// Flush the writer to ensure all data is written to the buffer
	writer.Flush()

	// Print the CSV content (optional for debugging)
	fmt.Println("----------------------------------------------------------------")
	fmt.Printf("CSV Content in Email: %s\n", csvBuffer.String())

	// Create the MIME message with attachment
	msg := "From: " + dynamic.Values + "\r\n" +
		"To: " + to + "\r\n" +
		"Cc: " + cc + "\r\n" +
		"Subject: " + subject + "\r\n" +
		"MIME-version: 1.0\r\n" +
		"Content-Type: multipart/mixed; boundary=boundarystring\r\n\r\n" +
		"--boundarystring\r\n" +
		"Content-Type: text/plain; charset=\"UTF-8\"\r\n\r\n" +
		body + "\r\n\r\n" + // Body content

		"--boundarystring\r\n" +
		"Content-Type: text/csv; charset=utf-8\r\n" +
		"Content-Disposition: attachment; filename=petition_list.csv\r\n\r\n" +
		csvBuffer.String() + "\r\n" + // CSV content

		"--boundarystring--"

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, dynamic.Values, []string{to, cc}, []byte(msg))
	if err != nil {
		fmt.Printf("Error sending email: %v\n", err)
		return fmt.Errorf("error sending email: %v", err)
	}

	// Return any errors encountered during writing to the buffer
	if err := writer.Error(); err != nil {
		fmt.Printf("Error writing to CSV buffer: %v\n", err)
		return fmt.Errorf("error writing to CSV buffer: %v", err)
	}

	return nil
}

// SendPartyInquiryEmail sends an email for the party inquiry
func SendPartyInquiryEmail(reqObj gen.InsertPartyInquiryParams, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum, dynamic4 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic3.Values)

	// Recipient and message details
	to := dynamic2.Values
	subject := "New Party Inquiry"
	from := dynamic.Values
	body := fmt.Sprintf("新しいパーティーの問い合わせ:\n\nユーザー名: %s\nメールアドレス: %s\nメッセージ: %s\n", reqObj.UserName, reqObj.EmailID, reqObj.MessageContent)

	// Add a reply-to header with the user's email address
	headers := map[string]string{
		"From":     from,
		"To":       to,
		"Subject":  subject,
		"Reply-To": reqObj.EmailID,
	}
	msg := ""
	for key, value := range headers {
		msg += fmt.Sprintf("%s: %s\r\n", key, value)
	}
	msg += "\r\n" + body

	// SMTP server configuration
	smtpServer := dynamic3.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, []string{to}, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}

	fmt.Println("Mail Sent")
	return err
}

// Function to send email notification
func SendAgendaReportEmail(messageObj *gen.SelectUserInfoByUserIdRow, messageObj2 *gen.AgendaComment, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum, dynamic4 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic3.Values)

	// Recipient and message details
	to := dynamic2.Values
	subject := "Agenda Reported"
	from := dynamic.Values
	body := fmt.Sprintf("ユーザーからのアジェンダが報告されました:\n\nユーザー名: %s\nアジェンダコメントテキスト: %s\n", messageObj.HandleName, messageObj2.CommentText)

	// Compose the email
	msg := fmt.Sprintf("From: %s\r\nTo:%s\r\nSubject: %s\r\n\r\n%s", from, to, subject, body)

	// SMTP server configuration
	smtpServer := dynamic3.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, []string{to}, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}

	fmt.Println("Mail Sent")
	return err
}

func SendPaymentSuccessEmail(EmailID, name string, membershipCompletionDate time.Time, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := []string{EmailID}
	subject := "JPP Membership Payment"
	from := dynamic.Values
	body := "親愛なる" + name + "様、\n\n" +
		"会費のお支払いありがとうございました。お支払いが正常に完了し、会員資格が有効になりました。有効期限は" + membershipCompletionDate.Format("2006-01-02") + "まで、1年間です。\n" +
		"ご質問やご不明な点がございましたら、お気軽にお問い合わせください。\n\n" +
		"敬具、\n" +
		"JPP会員開発チーム"

	// Compose the email
	msg := fmt.Sprintf("To: %s\r\nFrom:%s\r\nSubject: %s\r\n\r\n%s", to[0], from, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, to, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}

	fmt.Println("Mail Sent")
	return err
}

func SendDonationPaymentSuccessEmail(EmailID, name string, dynamic *gen.DynamicDatum, dynamic1 *gen.DynamicDatum, dynamic2 *gen.DynamicDatum, dynamic3 *gen.DynamicDatum) error {
	// Sender's authentication credentials
	auth := smtp.PlainAuth("", dynamic.Values, dynamic1.Values, dynamic2.Values)

	// Recipient and message details
	to := []string{EmailID}
	subject := "JPP Donation Payment"
	from := dynamic.Values
	body := "親愛なる" + name + "様へ、\n\n" +
		"ご寄付ありがとうございます！\n" +
		"ご寄付は正常に処理され、あなたのサポートに感謝いたします。\n" +
		"党運営・活動資金として大切に利用させていただきます。\n\n" +
		"ご質問やご不明点があれば、お気軽にお問い合わせください。\n\n" +
		"敬具、\n" +
		"JPP メンバーシップ開発チーム"

	// Compose the email
	msg := fmt.Sprintf("To: %s\r\nFrom:%s\r\nSubject: %s\r\n\r\n%s", to[0], from, subject, body)

	// SMTP server configuration
	smtpServer := dynamic2.Values
	smtpPort := 587

	// Connect to the SMTP server and send the email
	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)
	err := smtp.SendMail(add, auth, from, to, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}

	fmt.Println("Mail Sent")
	return err
}

// func SendTestEmail() {
// 	email := gen.SmsStatus{
// 		Email:        "sheikhaleeth.s@zenitus.com",
// 		EmailSubject: "Test Email",
// 		Body:         "This is a test email body.",
// 	}

// 	err := models.SendMail(email)
// 	if err != nil {
// 		fmt.Println("Error sending email:", err)
// 		return
// 	}

// 	fmt.Println("Email sent successfully!")
// }
