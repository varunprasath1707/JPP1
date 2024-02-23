package controller

import (
	"ZenitusJPP/database/postgres/gen"
	"ZenitusJPP/models"
	"ZenitusJPP/service"
	"fmt"
	"time"

	// service "ZenitusJPP/service"
	"encoding/json"
	"errors"
	"net/http"
	"strconv"
	"strings"
)

type MasterUser struct {
	MasterUser  service.MasterUser
	UserQuerier gen.UserQuerier
	// DatabaseDemoQuerier gen.DatabsedemoQuerier
}

func (c *MasterUser) UserIP(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.IPObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	if service.CheckBlankParameter(ReqObj.UserIP) {
		err := errors.New("user IP Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// cnt, err := c.UserQuerier.CheckIP(req.Context(), ReqObj.UserIP)
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	// if cnt == 0 {
	c.UserQuerier.AddIP(req.Context(), ReqObj.UserIP)
	// }
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "User IP Successfully",
	})
}

func (c *MasterUser) UserRegisteration(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.InsertUserInfoParamsObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Validation
	if service.CheckBlankParameter(ReqObj.FirstName) {
		err := errors.New("first name Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "名パラメータがありません")
		return
	}

	if service.CheckBlankParameter(ReqObj.LastName) {
		err := errors.New("last name Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "姓パラメータがありません")
		return
	}

	if service.CheckBlankParameter(ReqObj.HandleName) {
		err := errors.New("handle name Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ハンドル名パラメータがありません")
		return
	}
	// Check if handle name already exists
	userNameExists, err := c.UserQuerier.SelectUserCountByUsername(req.Context(), ReqObj.HandleName)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userNameExists != 0 {
		opterr := errors.New("handle Name already in use")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, opterr, "ハンドル名はすでに使用されています")
		return
	}

	if service.CheckBlankParameter(ReqObj.EmailID) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "電子メール ID パラメータがありません")
		return
	}
	// Convert email to lowercase
	ReqObj.EmailID = strings.ToLower(ReqObj.EmailID)

	// Email Validation
	mail := service.IsValidEmail(ReqObj.EmailID)
	if !mail {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid email address, Email id has should contain @ after at one character and .com or .in must"))
		return
	}

	// Check if email already exists
	userExists, err := c.UserQuerier.SelectUserCountByEmail(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userExists != 0 {
		opterr := errors.New("email address already in use")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, opterr, "使ってるメアド")
		return
	}

	if service.CheckBlankParameter(string(ReqObj.UserAddress)) {
		err := errors.New("userAddress Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーアドレスパラメータがありません")
		return
	}

	if service.CheckBlankParameter(string(ReqObj.Gender)) {
		err := errors.New("gender Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "性別パラメータがありません")
		return
	}

	// Check if Date of Birth is missing
	if ReqObj.Dob.IsZero() {
		err := errors.New("date of Birth parameter is missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "生年月日パラメータがありません")
		return
	}

	if ReqObj.Dob.After(time.Now()) {
		err := errors.New("invalid date of birth")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if phone number is provided before validating
	if ReqObj.PhMobile != "" {
		phMobileStr := ReqObj.PhMobile
		if service.IsValidMobileNumber(phMobileStr) && len(phMobileStr) >= 10 {
			fmt.Println("Valid mobile number!")
		} else {
			fmt.Println("Invalid mobile number!")
			// Add an error response for invalid mobile number
			err := errors.New("mobile number contains a minimum of 10 digits")
			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return
		}
	}

	if service.CheckBlankParameter(string(ReqObj.JppPassword)) {
		err := errors.New("password Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Password validation
	pass := service.IsValidPassword(ReqObj.JppPassword)
	if !pass {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid Password, password should be at least 8 characters long and contain at least one uppercase letter, one numeric digit, and one special character"))
		return
	}

	var CodeReqObj gen.InsertUserInfoParams
	CodeReqObj.PgpSymEncrypt = string(ReqObj.FirstName)
	CodeReqObj.PgpSymEncrypt_2 = string(ReqObj.LastName)
	CodeReqObj.PgpSymEncrypt_3 = string(ReqObj.HandleName)
	CodeReqObj.DisplayName = ReqObj.DisplayName
	CodeReqObj.EmailID = ReqObj.EmailID
	CodeReqObj.Gender = ReqObj.Gender
	CodeReqObj.Dob = ReqObj.Dob
	CodeReqObj.PgpSymEncrypt_4 = string(ReqObj.UserAddress)
	CodeReqObj.PgpSymEncrypt_5 = string(ReqObj.PhFixed)
	CodeReqObj.PgpSymEncrypt_6 = string(ReqObj.PhMobile)
	CodeReqObj.JppPassword = ReqObj.JppPassword
	CodeReqObj.IsAdmin = ReqObj.IsAdmin
	CodeReqObj.MobileVerification = ReqObj.MobileVerification

	_, err = c.UserQuerier.InsertUserInfo(req.Context(), CodeReqObj)
	fmt.Println("Process B :", err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	OTP, err := strconv.ParseInt(service.GenerateOTPCode(), 10, 64)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	fmt.Println("opt 1", OTP)
	OTP2, err := strconv.ParseInt(service.GenerateOTPCode(), 10, 64)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println("opt 2", OTP2)

	//getting data from dynamic values using keys
	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Send Email OTP to the user's email
	err = service.SendEmail(ReqObj.EmailID, strconv.FormatInt(OTP, 10), ReqObj.HandleName, obj[0], obj1[0], obj2[0], obj3[0])
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// // Send Mobile OTP to the user's email
	// err = service.SendEmail(ReqObj.EmailID, strconv.FormatInt(OTP2, 10), ReqObj.HandleName, obj[0], obj1[0], obj2[0], obj3[0])
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	_, err = c.UserQuerier.InsertSMS(req.Context(), gen.InsertSMSParams{
		Email:        ReqObj.EmailID,
		Body:         "JPP Registration",
		EmailSubject: "Mobile OTP for the JPP Registration",
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	err = c.UserQuerier.UpdateOTPByEmailIDID(req.Context(), gen.UpdateOTPByEmailIDIDParams{
		EmailID:  ReqObj.EmailID,
		Otp:      int32(OTP2),
		OtpEmail: int32(OTP),
	})

	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		JapaneseMessage: "ユーザー登録が完了しました",
		Message:         "User Registered Successfully",
	})
}

func (c *MasterUser) Checkmobileotp(resp http.ResponseWriter, req *http.Request) {
	type SelectUserInfoByMobileEmailIDInfoParamsObj struct {
		EmailID   string `json:"emailId"`
		MobileNo  string `json:"phMobile"`
		MobileOTP int64  `json:"mobileotp"`
	}

	var ReqObj SelectUserInfoByMobileEmailIDInfoParamsObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	if service.CheckBlankParameter(string((ReqObj.MobileNo))) {
		err := errors.New("mobile No Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	cnt, err := c.UserQuerier.SelectUserCountByMobileNoEmail(req.Context(), gen.SelectUserCountByMobileNoEmailParams{
		EmailID: ReqObj.EmailID,
		Column2: string(ReqObj.MobileNo),
	})
	fmt.Println(ReqObj.EmailID)
	fmt.Println(ReqObj.MobileNo)
	fmt.Println(cnt)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if cnt != 1 {
		opterr := errors.New("mobile No does not exists")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	if service.CheckBlankParameter(string((ReqObj.MobileOTP))) {
		err := errors.New("otp Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	UserObj, Err := c.UserQuerier.SelectUserInfoByMobileEmailIDInfo(req.Context(), gen.SelectUserInfoByMobileEmailIDInfoParams{
		EmailID: ReqObj.EmailID,
		Column2: string(ReqObj.MobileNo),
	})
	fmt.Println(UserObj)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if UserObj.Otp != int32(ReqObj.MobileOTP) {
		opterr := errors.New("OTP does not match")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	err = c.UserQuerier.UpdateVerifiedMobileByOTP(req.Context(), string(ReqObj.MobileNo))
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Successfully user verified",
	})
}

func (c *MasterUser) Checkotp(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdateOTPByEmailIDIDParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Convert email to lowercase
	ReqObj.EmailID = strings.ToLower(ReqObj.EmailID)
	// Validation
	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	cnt, err := c.UserQuerier.SelectUserCountByEmail(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if cnt != 1 {
		opterr := errors.New("email Id does not exists")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Otp)) {
		err := errors.New("otp Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailID)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println("user data otp", UserObj.OtpEmail)
	fmt.Println("new data otp", ReqObj.OtpEmail)
	if UserObj.OtpEmail != ReqObj.OtpEmail {
		opterr := errors.New("OTP does not match")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}

	err = c.UserQuerier.UpdateVerifyEmailByOTP(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Successfully user verified",
	})
}

func (c *MasterUser) UserForgetPassword(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.SelectUserInfoByEmailRow
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	ReqObj.EmailID = strings.ToLower(ReqObj.EmailID)

	// Validation
	cnt, err := c.UserQuerier.SelectUserCountByEmail(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if cnt != 1 {
		opterr := errors.New("email Id does not exists")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	OTP, err := strconv.ParseInt(service.GenerateOTPCode(), 10, 64)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	OTP2, err := strconv.ParseInt(service.GenerateOTPCode(), 10, 64)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Send OTP to the user's email
	err = service.ReSendEmailOTP(ReqObj.EmailID, strconv.FormatInt(OTP, 10), ReqObj.HandleName, obj[0], obj1[0], obj2[0], obj3[0])
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	err = c.UserQuerier.UpdateOTPByEmailIDID(req.Context(), gen.UpdateOTPByEmailIDIDParams{
		EmailID:  ReqObj.EmailID,
		OtpEmail: int32(OTP),
		Otp:      int32(OTP2),
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "OTP send successfully",
	})
}

func (c *MasterUser) ResendMobileOtp(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.SelectUserInfoByEmailRow
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	ReqObj.EmailID = strings.ToLower(ReqObj.EmailID)

	// Validation
	cnt, err := c.UserQuerier.SelectUserCountByEmail(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if cnt != 1 {
		opterr := errors.New("email Id does not exists")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	OTP, err := strconv.ParseInt(service.GenerateOTPCode(), 10, 64)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// //getting data from dynamic values using keys
	// obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// //getting data from dynamic values using keys
	// obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// //getting data from dynamic values using keys
	// obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// //getting data from dynamic values using keys
	// obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// // Send OTP to the user's email
	// err = service.ReSendEmailForMobileOtp(ReqObj.EmailID, strconv.FormatInt(OTP, 10), ReqObj.HandleName, obj[0], obj1[0], obj2[0], obj3[0])
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	_, err = c.UserQuerier.InsertSMS(req.Context(), gen.InsertSMSParams{
		Email:        ReqObj.EmailID,
		Body:         "JPP Registration",
		EmailSubject: "Mobile OTP for the JPP Registration",
	})

	err = c.UserQuerier.UpdateOTPByEmailIDID(req.Context(), gen.UpdateOTPByEmailIDIDParams{
		EmailID: ReqObj.EmailID,
		Otp:     int32(OTP),
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "OTP send successfully",
	})
}

func (c *MasterUser) UserUpdatePassword(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdateUserPasswordByEmailIDOTPParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Email Validation
	mail := service.IsValidEmail(ReqObj.EmailID)
	if !mail {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid email address"))
		return
	}

	if service.CheckBlankParameter(string(ReqObj.JppPassword)) {
		err := errors.New("password Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Password validation
	pass := service.IsValidPassword(ReqObj.JppPassword)
	if !pass {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid password. Password should be at least 8 characters long and contain at least one uppercase letter, one numeric digit, and one special character"))
		return
	}

	if service.CheckBlankParameter(string(ReqObj.OtpEmail)) {
		err := errors.New("otp Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailID)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, Err)
		return
	}
	if UserObj.OtpEmail != ReqObj.OtpEmail {
		opterr := errors.New("OTP does not match")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}
	err = c.UserQuerier.UpdateUserPasswordByEmailIDOTP(req.Context(), gen.UpdateUserPasswordByEmailIDOTPParams{
		EmailID:     ReqObj.EmailID,
		JppPassword: ReqObj.JppPassword,
		OtpEmail:    ReqObj.OtpEmail,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Password Successfully Updated",
	})

}
func (c *MasterUser) Login(resp http.ResponseWriter, req *http.Request) {

	var ReqObj gen.SelectUserIDByEmailPasswordParams

	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Validation
	if service.CheckBlankParameter(ReqObj.EmailID) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(ReqObj.JppPassword) {
		err := errors.New("password Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	ReqObj.EmailID = strings.TrimSpace(ReqObj.EmailID)
	ReqObj.JppPassword = strings.TrimSpace(ReqObj.JppPassword)

	//Validation
	cnt, err := c.UserQuerier.SelectUserIDByEmailPassword(req.Context(), gen.SelectUserIDByEmailPasswordParams{

		EmailID:     ReqObj.EmailID,
		JppPassword: ReqObj.JppPassword,
	})

	if err != nil {

		if err.Error() == "no rows in result set" {
			err = errors.New("invalid credentials info")
			service.WriteResponseErr(resp, http.StatusUnauthorized, err)
			return

		} else {

			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return

		}
	}

	// Check if email is verified
	if !cnt.EmailVerification {
		err = errors.New("email not verified")
		service.WriteResponseErr(resp, http.StatusNonAuthoritativeInfo, err)
		return
	}

	var l service.IAuthentication
	token, err := service.IAuthentication.JWTToken(l, models.JWTUserDetails{
		UserID: string(rune(cnt.UserID)),
		ID:     cnt.UserID,
		Email:  ReqObj.EmailID,
		Name:   fmt.Sprintf("%v", "Not Added"),
	}, "GetToken")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	err = c.UserQuerier.UpdateTokenByID(req.Context(), gen.UpdateTokenByIDParams{
		UserID: cnt.UserID,
		Token:  token,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "User Logged successfully",
		Token:        token,
		ResponseData: cnt,
	})
}

func (c *MasterUser) Logout(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdateTokenByIDParams

	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New(" user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if the user with the given UserID exists
	userCount, err := c.UserQuerier.SelectUserCountByID(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userCount == 0 {
		err := errors.New("user ID does not exist. Please enter a correct User ID")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Update the token by setting it to an empty string
	err = c.UserQuerier.UpdateTokenByID(req.Context(), gen.UpdateTokenByIDParams{
		Token:  "",
		UserID: ReqObj.UserID,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "User Logout successfully",
	})
}

func (c *MasterUser) UserProfile(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.UserObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New(" user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed User Details",
		ResponseData: MessageObj,
	})
}

func (c *MasterUser) UserProfileUpdate(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.UpdateUserInfoByIDParamsObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New(" user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	if service.CheckBlankParameter(ReqObj.FirstName) {
		err := errors.New("first name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if service.CheckBlankParameter(ReqObj.LastName) {
		err := errors.New("last name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if service.CheckBlankParameter(string(ReqObj.UserAddress)) {
		err := errors.New("userAddress Parameter Is Missing")
		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーアドレスパラメータがありません")
		return
	}

	if service.CheckBlankParameter(string(ReqObj.Gender)) {
		err := errors.New("gender Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if ReqObj.PhMobile != "" {
		// mobile number validation
		phMobileStr := ReqObj.PhMobile
		if service.IsValidMobileNumber(phMobileStr) && len(phMobileStr) >= 10 {
			fmt.Println("Valid mobile number!")
		} else {
			fmt.Println("Invalid mobile number!")
			// Add an error response for invalid mobile number
			err := errors.New("mobile number contain minimum 10")
			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return
		}
	}
	var DataReqObj gen.UpdateUserInfoByIDParams
	DataReqObj.UserID = ReqObj.UserID
	DataReqObj.PgpSymEncrypt = ReqObj.FirstName
	DataReqObj.PgpSymEncrypt_2 = ReqObj.LastName
	DataReqObj.DisplayName = ReqObj.DisplayName
	DataReqObj.Gender = ReqObj.Gender
	DataReqObj.Dob = ReqObj.Dob
	DataReqObj.PgpSymEncrypt_3 = ReqObj.UserAddress
	DataReqObj.PgpSymEncrypt_4 = ReqObj.PhFixed
	DataReqObj.PgpSymEncrypt_5 = ReqObj.PhMobile
	DataReqObj.MobileVerification = ReqObj.MobileVerification

	err = c.UserQuerier.UpdateUserInfoByID(req.Context(), DataReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully updated user",
	})
}

func (c *MasterUser) UserChangePassword(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdateUserPasswordParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailID)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New(" user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if EmailID is blank
	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New(" email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Convert email to lowercase
	ReqObj.EmailID = strings.ToLower(ReqObj.EmailID)

	// Check if email already exists
	userExists, err := c.UserQuerier.SelectUserCountByEmail(req.Context(), ReqObj.EmailID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userExists != 1 {
		opterr := errors.New("email address Does not exsist")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}

	// Validation: Check if Password2 is blank
	if service.CheckBlankParameter(string(ReqObj.JppPassword_2)) {
		err := errors.New(" password2 Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Password validation
	pass2 := service.IsValidPassword(ReqObj.JppPassword_2)
	if UserObj.JppPassword != ReqObj.JppPassword_2 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("old Password is  incorrect Please enter correct password to change new password"))
		return
	} else if !pass2 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid Password, password should be at least 8 characters long and contain at least one uppercase letter, one numeric digit, and one special character"))
		return
	}

	// Validation: Check if Password is blank
	if service.CheckBlankParameter(string(ReqObj.JppPassword)) {
		err := errors.New(" password Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Password validation
	pass1 := service.IsValidPassword(ReqObj.JppPassword)
	if !pass1 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("invalid Password, password should be at least 8 characters long and contain at least one uppercase letter, one numeric digit, and one special character"))
		return
	}

	err = c.UserQuerier.UpdateUserPassword(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully updated user password",
	})
}

func (c *MasterUser) SMS(resp http.ResponseWriter, req *http.Request) {
	// Call SendMail function
	err := models.SendMail()
	if err != nil {
		fmt.Println("Error sending email:", err)
		return
	}

	fmt.Println("Email sent successfully!")
}

// func (c *MasterUser) PaymentAdd(resp http.ResponseWriter, req *http.Request) {
// 	var ReqObj models.InsertTransactionParamsObj
// 	err := json.NewDecoder(req.Body).Decode(&ReqObj)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	// // Validation
// 	// if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
// 	// 	err := errors.New("userID Parameter Is Missing")
// 	// 	service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーIDパラメータがありません")
// 	// 	return
// 	// }

// 	// if service.CheckBlankParameter(string(ReqObj.TransactionToken)) {
// 	// 	err := errors.New("token Parameter Is Missing")
// 	// 	service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "トークンパラメータがありません")
// 	// 	return
// 	// }

// 	// if service.CheckBlankParameter(string(ReqObj.TransactionUrl)) {
// 	// 	err := errors.New("url Parameter Is Missing")
// 	// 	service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "URLパラメータがありません")
// 	// 	return
// 	// }

// 	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	// Example: Check if the UserID exists in the system
// 	if ReqObj.UserID > 0 {
// 		userExists, err := c.UserQuerier.SelectUserCountByID(req.Context(), ReqObj.UserID)
// 		if err != nil {
// 			service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 			return
// 		}
// 		if userExists == 0 {
// 			err := errors.New("user does not exist")
// 			service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーが存在しません")
// 			return
// 		}
// 	}

// 	// var ReqObjObj gen.InsertTransactionParams
// 	// ReqObjObj.UserID = ReqObj.UserID
// 	// ReqObjObj.TransactionUrl = ReqObj.TransactionUrl
// 	// ReqObjObj.TransactionToken = ReqObj.TransactionToken

// 	// _, err = c.UserQuerier.InsertTransaction(req.Context(), ReqObjObj)
// 	// if err != nil {
// 	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 	// 	return
// 	// }

// 	// MessageObj2, err := c.UserQuerier.SelectTransactionByUserID(req.Context(), ReqObj.UserID)
// 	// if err != nil {
// 	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 	// 	return
// 	// }

// 	// // Append data from MessageObj2 to a string
// 	// appendedData := ""

// 	// for _, transaction := range MessageObj2 {
// 	// 	appendedData += transaction.TransactionToken // Assuming there's a field named TransactionData
// 	// }

// 	// if appendedData != ReqObj.TransactionToken {
// 	// 	opterr := errors.New("token does not match")
// 	// 	service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
// 	// 	return
// 	// }

// 	// err = c.UserQuerier.UpdateTransactionById(req.Context(), gen.UpdateTransactionByIdParams{
// 	// 	UserID:            ReqObj.UserID,
// 	// 	TransactionToken:  ReqObj.TransactionToken,
// 	// 	TransactionStatus: true,
// 	// })

// 	// if err != nil {
// 	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 	// 	return
// 	// }
// 	// service.WriteResponse(resp, models.ResponseObj{
// 	// 	Message: "successfully updated status",
// 	// })

// 	// // Update payment token in the database
// 	// err = c.UserQuerier.UpdatePaymentTokenByEmailIDID(req.Context(), gen.UpdatePaymentTokenByEmailIDIDParams{
// 	// 	EmailID:      MessageObj.EmailID,
// 	// 	PaymentToken: ReqObj.TransactionToken,
// 	// 	IsPaid:       true,
// 	// 	PaidDate: Database.NullTime{
// 	// 		NullTime: sql.NullTime{
// 	// 			Time:  time.Now(),
// 	// 			Valid: true,
// 	// 		},
// 	// 	},
// 	// })

// 	// if err != nil {
// 	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 	// 	return
// 	// }
// 	// service.WriteResponse(resp, models.ResponseObj{
// 	// 	Message: "successfully updated paid status in user",
// 	// })

// 	//getting data from dynamic values using keys
// 	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	membershipCompletionDate, ok := MessageObj.MembershipCompletionDate.(time.Time)
// 	if !ok {
// 		// Handle the case where the assertion fails (e.g., log an error)
// 		err := errors.New("failed to assert MembershipCompletionDate as time.Time")
// 		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 		return
// 	}

// 	fullName := MessageObj.FirstName + " " + MessageObj.LastName

// 	// Send payment success email
// 	err = service.SendPaymentSuccessEmail(MessageObj.EmailID, fullName, membershipCompletionDate, obj[0], obj1[0], obj2[0], obj3[0])
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 		return
// 	}

// 	service.WriteResponse(resp, models.ResponseObj{
// 		JapaneseMessage: "支払いが正常に追加されました",
// 		Message:         "Payment Added Successfully",
// 	})
// }

// func (c *MasterUser) DonationAdd(resp http.ResponseWriter, req *http.Request) {
// 	var ReqObj gen.InsertDonationTransactionParams
// 	err := json.NewDecoder(req.Body).Decode(&ReqObj)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	// Validation
// 	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
// 		err := errors.New("userID Parameter Is Missing")
// 		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーIDパラメータがありません")
// 		return
// 	}

// 	if service.CheckBlankParameter(string(ReqObj.TransactionToken)) {
// 		err := errors.New("token Parameter Is Missing")
// 		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "トークンパラメータがありません")
// 		return
// 	}

// 	if service.CheckBlankParameter(string(ReqObj.TransactionUrl)) {
// 		err := errors.New("url Parameter Is Missing")
// 		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "URLパラメータがありません")
// 		return
// 	}

// 	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	// Example: Check if the UserID exists in the system
// 	userExists, err := c.UserQuerier.SelectUserCountByID(req.Context(), ReqObj.UserID)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 		return
// 	}
// 	if userExists == 0 {
// 		err := errors.New("user does not exist")
// 		service.WriteResponseErrjapan(resp, http.StatusBadRequest, err, "ユーザーが存在しません")
// 		return
// 	}

// 	_, err = c.UserQuerier.InsertDonationTransaction(req.Context(), ReqObj)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	MessageObj2, err := c.UserQuerier.SelectTDonationTransactionByUserID(req.Context(), ReqObj.UserID)
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	// Append data from MessageObj2 to a string
// 	appendedData := ""

// 	for _, transaction := range MessageObj2 {
// 		appendedData += transaction.TransactionToken // Assuming there's a field named TransactionData
// 	}

// 	if appendedData != ReqObj.TransactionToken {
// 		opterr := errors.New("token does not match")
// 		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
// 		return
// 	}

// 	err = c.UserQuerier.UpdateDonationTransactionById(req.Context(), gen.UpdateDonationTransactionByIdParams{
// 		UserID:            ReqObj.UserID,
// 		TransactionToken:  ReqObj.TransactionToken,
// 		TransactionStatus: true,
// 	})

// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}
// 	service.WriteResponse(resp, models.ResponseObj{
// 		Message: "successfully updated status",
// 	})

// 	//getting data from dynamic values using keys
// 	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	//getting data from dynamic values using keys
// 	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusBadRequest, err)
// 		return
// 	}

// 	membershipCompletionDate, ok := MessageObj.MembershipCompletionDate.(time.Time)
// 	if !ok {
// 		// Handle the case where the assertion fails (e.g., log an error)
// 		err := errors.New("failed to assert MembershipCompletionDate as time.Time")
// 		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 		return
// 	}

// 	fullName := MessageObj.FirstName + " " + MessageObj.LastName

// 	// Send payment success email
// 	err = service.SendDonationPaymentSuccessEmail(MessageObj.EmailID, fullName, membershipCompletionDate, obj[0], obj1[0], obj2[0], obj3[0])
// 	if err != nil {
// 		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
// 		return
// 	}

// 	service.WriteResponse(resp, models.ResponseObj{
// 		JapaneseMessage: "寄付が正常に追加されました",
// 		Message:         "Donation Added Successfully",
// 	})
// }
