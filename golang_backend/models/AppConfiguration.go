package models

import (
	"ZenitusJPP/database/postgres/gen"
	"fmt"
	"net/smtp"
	"time"

	"github.com/dgrijalva/jwt-go"
)

type AppConfiguration struct {
	ID          int64     `json:"id,omitempty"`
	ConfigCode  string    `json:"configCode,omitempty"`
	ConfigKey   int64     `json:"configKey,omitempty"`
	ConfigValue float32   `json:"configValue,omitempty"`
	CreatedOn   time.Time `json:"createdOn,omitempty"`
}

type JwtClaim struct {
	jwt.StandardClaims
	Name       string
	Username   string
	UserID     string
	Identifier int64
	Email      string
}

func (jtc *JwtClaim) SetIssuer(issuer string) {
	jtc.Issuer = issuer
}

func (jtc *JwtClaim) SetIssuedAt() {
	jtc.IssuedAt = time.Now().UTC().UnixNano()
}

func (jtc *JwtClaim) SetExpiry() {
	jtc.ExpiresAt = time.Now().UTC().UnixNano()
}

type JWTUserDetails struct {
	UserID string
	ID     int64
	Name   string
	Email  string
}

type ResponseObj struct {
	HasError        bool        `json:"haserror,omitempty"`
	Message         string      `json:"message,omitempty"`
	JapaneseMessage string      `json:"japanesemessage,omitempty"`
	ErrorMessage    string      `json:"errormessage,omitempty"`
	Token           string      `json:"token,omitempty"`
	ResponseData    interface{} `json:"responsedata,omitempty"`
}

type ResponseObj2 struct {
	HasError     bool        `json:"haserror,omitempty"`
	Message      string      `json:"message,omitempty"`
	ErrorMessage string      `json:"errormessage,omitempty"`
	PaymentToken string      `json:"paymenttoken,omitempty"`
	ResponseData interface{} `json:"responsedata,omitempty"`
}

type Response struct {
	Data ResponseObj `json:"data,omitempty"`
}

type AgendaResponseObj struct {
	PreApproved    interface{} `json:"preapproved,omitempty"`
	ApprovedAgenda interface{} `json:"approvedagenda,omitempty"`
}

type AgendaObj struct {
	AgendaMasterID int64 `json:"agendamasterid,omitempty"`
}

type AgendaDetailObj struct {
	AgendaDetail  interface{} `json:"agendadetail,omitempty"`
	AgendaComment interface{} `json:"agendacomment,omitempty"`
	GraphData     interface{} `json:"graphdata,omitempty"`
}

type AgendaListOprion struct {
	OptionListing int64 `json:"optionlisting,omitempty"`
}

type IPObj struct {
	UserIP string `json:"userip,omitempty"`
}

type SelectLocations string

type InsertAgendaInfoParamsObj struct {
	TopicName         string              `json:"topicName"`
	LocationLevel     gen.SelectLocations `json:"locationLevel"`
	DiscussionDetails string              `json:"discussionDetails"`
	UserID            int64               `json:"userId"`
	AgnedaChoices     string              `json:"agnedachoices"`
}

type MobileOTPOBJ struct {
	MobileNo  int64 `json:"mobileno,omitempty"`
	MobileOTP int64 `json:"mobileotp,omitempty"`
}

type PetitionObj struct {
	PetitionMasterID int64 `json:"petitionMasterId,omitempty"`
}

type UserObj struct {
	UserID int64 `json:"user_id,omitempty"`
}

type PetitionStruct struct {
	PetitionMasterID int64  `json:"petitionMasterId,omitempty"`
	Email            string `json:"emailId,omitempty"`
	CommentText      string `json:"commentText,omitempty"`
}

type NotificationObj struct {
	NotificationID int64 `json:"notificationId,omitempty"`
}

type PetitionFilterObj struct {
	Petitionfilter int64 `json:"filter,omitempty"`
}

type PaymentObj struct {
	EmailId string `json:"emailId,omitempty"`
	Token   string `json:"token,omitempty"`
}

type InsertTransactionParamsObj struct {
	UserID           int64  `json:"userId"`
	EmailId          string `json:"emailId,omitempty"`
	TransactionUrl   string `json:"transactionUrl"`
	TransactionToken string `json:"transactionToken"`
}

type UserResponse struct {
	UserID             int64     `json:"userId"`
	FirstName          string    `json:"firstName"`
	LastName           string    `json:"lastName"`
	UserAddress        string    `json:"userAddress"`
	PhFixed            string    `json:"phFixed"`
	PhMobile           string    `json:"phMobile"`
	HandleName         string    `json:"handleName"`
	DisplayName        string    `json:"displayName"`
	EmailID            string    `json:"emailId"`
	Token              string    `json:"token"`
	Gender             string    `json:"gender"`
	Dob                time.Time `json:"dob"`
	JppPassword        string    `json:"jppPassword"`
	Otp                int32     `json:"otp"`
	IsAdmin            bool      `json:"isAdmin"`
	MobileVerification bool      `json:"mobileVerification"`
	IsDeleted          bool      `json:"isDeleted"`
	CreatedOn          time.Time `json:"createdOn"`
	LastUpdatedOn      time.Time `json:"lastUpdatedOn"`
	EmailVerification  bool      `json:"emailVerification"`
	OtpEmail           int32     `json:"otpEmail"`
	PaymentToken       string    `json:"paymentToken"`
	IsPaid             bool      `json:"isPaid"`
	PaidDate           string    `json:"paidDate"`
	LoginDatetime      string    `json:"loginDatetime"`
}

type InsertUserInfoParamsObj struct {
	FirstName          string           `json:"firstname"`
	LastName           string           `json:"lastname"`
	HandleName         string           `json:"handleName"`
	DisplayName        string           `json:"displayName"`
	EmailID            string           `json:"emailId"`
	Gender             gen.SelectGender `json:"gender"`
	Dob                time.Time        `json:"dob"`
	UserAddress        string           `json:"useraddress"`
	PhFixed            string           `json:"phfixed"`
	PhMobile           string           `json:"phmobile"`
	JppPassword        string           `json:"jppPassword"`
	IsAdmin            bool             `json:"isAdmin"`
	MobileVerification bool             `json:"mobileVerification"`
}

type UpdateUserInfoByIDParamsObj struct {
	UserID             int64            `json:"userId"`
	FirstName          string           `json:"firstname"`
	LastName           string           `json:"lastname"`
	DisplayName        string           `json:"displayName"`
	Gender             gen.SelectGender `json:"gender"`
	Dob                time.Time        `json:"dob"`
	UserAddress        string           `json:"useraddress"`
	PhFixed            string           `json:"phfixed"`
	PhMobile           string           `json:"phmobile"`
	MobileVerification bool             `json:"mobileVerification"`
}

type SelectUserCountByMobileNoEmailParamsObj struct {
	EmailID  string `json:"emailId"`
	PhMobile int64  `json:"phMobile"`
}

type DonationObj struct {
	UserID           int64       `json:"userId"`
	TransactionUrl   string      `json:"transactionUrl"`
	TransactionToken string      `json:"transactionToken"`
	UserName         string      `json:"userName"`
	UserEmail        string      `json:"userEmail"`
	UserAmount       interface{} `json:"userAmount"`
}

type DonationObj2 struct {
	UserID   int64  `json:"userId"`
	EmailId  string `json:"emailId,omitempty"`
	Token    string `json:"token,omitempty"`
	UserName string `json:"userName,omitempty"`
}

func SendMail() error {

	// Set up authentication information.
	auth := smtp.PlainAuth("", "support@chokumin.com", "Ryuta7233#", "mail50.onamae.ne.jp")

	to := []string{"haleethtmmk@gmail.com"}
	subject := "Test Email"
	body := "This is a test email body."
	from := "support@chokumin.com"

	msg := fmt.Sprintf("To: %s\r\nFrom:%s\r\nSubject: %s\r\n\r\n%s", to[0], from, subject, body)

	smtpServer := "mail50.onamae.ne.jp"
	smtpPort := 587

	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)

	err := smtp.SendMail(add, auth, from, to, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")
	return err
}
