package cron

import (
	Database "ZenitusJPP/database"
	"ZenitusJPP/database/postgres"
	"ZenitusJPP/database/postgres/gen"
	"ZenitusJPP/models"
	"ZenitusJPP/service"
	"context"
	"fmt"
	"net/smtp"
	"time"
)

func CronDemo(ctx context.Context, postgresQuerier postgres.Querier) (err error) {
	fmt.Println("Testaaaaa : ", time.Now())
	obj, err := postgresQuerier.SelectNotice(ctx)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(obj)
	return
}

func CronUpdateLoginStatus(ctx context.Context, postgresQuerier postgres.Querier) (err error) {
	err = postgresQuerier.UpdateLoginStatus(ctx)
	if err != nil {
		fmt.Println(err)
		return
	}
	return
}

func convertDateTimeToString(obj Database.NullTime) (objval string, err error) {

	if obj.Valid {
		objval = (obj.Time).Local().Format("2006-01-02")
	}
	return
}

func CronYealySubscribtion(ctx context.Context, postgresQuerier postgres.Querier) (err error) {
	// This will send a mail to notify after 358 days (A week before the date)
	obj, err := postgresQuerier.MemberShipUser(ctx)
	if err != nil {
		fmt.Println(err)
		return
	}

	// var users []models.UserResponse
	var user models.UserResponse
	for _, userobj := range obj {
		user.UserID = userobj.UserID
		// user.FirstName = string(userobj.first_name)
		user.LastName = string(userobj.LastName)
		user.UserAddress = string(userobj.UserAddress)
		user.PhFixed = string(userobj.PhFixed)
		user.PhMobile = string(userobj.PhMobile)
		user.HandleName = string(userobj.HandleName)
		user.DisplayName = userobj.DisplayName
		user.EmailID = userobj.EmailID
		user.Token = userobj.Token
		user.Gender = string(userobj.Gender)
		user.Dob = userobj.Dob
		user.JppPassword = userobj.JppPassword
		user.Otp = userobj.Otp
		user.IsAdmin = userobj.IsAdmin
		user.MobileVerification = userobj.MobileVerification
		user.IsDeleted = userobj.IsDeleted
		user.CreatedOn = userobj.CreatedOn
		user.LastUpdatedOn = userobj.LastUpdatedOn
		user.EmailVerification = userobj.EmailVerification
		user.OtpEmail = userobj.OtpEmail
		user.PaymentToken = userobj.PaymentToken
		user.IsPaid = userobj.IsPaid
		user.PaidDate, _ = convertDateTimeToString(userobj.PaidDate)
		user.LoginDatetime, _ = convertDateTimeToString(userobj.LoginDatetime)
		// users = append(users, user)
	}

	//getting data from dynamic values using keys
	obj1, err := postgresQuerier.SelectDynamicDataByKey(ctx, "support-mail")
	if err != nil {
		fmt.Println(err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := postgresQuerier.SelectDynamicDataByKey(ctx, "support-pswd")
	if err != nil {
		fmt.Println(err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := postgresQuerier.SelectDynamicDataByKey(ctx, "SMPT")
	if err != nil {
		fmt.Println(err)
		return
	}

	//getting data from dynamic values using keys
	obj4, err := postgresQuerier.SelectDynamicDataByKey(ctx, "SMPT-PORT")
	if err != nil {
		fmt.Println(err)
		return
	}
	for _, userobj := range obj {
		fmt.Println(userobj.EmailID)
		err = service.EmailUserMembershipInfo(user, obj1[0], obj2[0], obj3[0], obj4[0])
		if err != nil {
			fmt.Println(err)
			return
		}
	}
	return
}

func HourlySMS(ctcx context.Context, postgresQuerier postgres.Querier) (err error) {
	fmt.Println("Starting : ", time.Now())
	// Delete old data
	err = postgresQuerier.DeleteOlderData(ctcx)
	if err != nil {
		fmt.Println("err a : ", err)
		// return
	}
	// Get existing data
	smsdata, err := postgresQuerier.SelectSMSData(ctcx)
	for _, values := range smsdata {
		// fmt.Println(keys)
		// fmt.Println(values)
		// Get count for user
		cntobj, err := postgresQuerier.SelectUserSMSCount(ctcx, values.Email)
		if err != nil {
			fmt.Println("err a : ", err)
			// return
		}
		fmt.Println(values.Email, " : ", cntobj, " : ", err)
		// If lesser then 3
		if cntobj < 3 {
			err = SendMail(*values)
			if err != nil {
				fmt.Println("err b : ", err)
			} else {
				err = postgresQuerier.UpdateSMS(ctcx, values.SmsID)
				if err != nil {
					fmt.Println("err c : ", err)
				}
				err = postgresQuerier.InsertUserSMS(ctcx, gen.InsertUserSMSParams{
					Cnt:    1,
					UserID: values.Email,
				})
				if err != nil {
					fmt.Println("err d : ", err)
				}
			}
		}
		// send SMS
		// Insert into user_sms
		// Update status sms_status
	}
	fmt.Println("Tested : ", time.Now())
	// gettime, err := postgresQuerier.SelectExecutedTime(ctcx)
	// if err != nil {
	// 	fmt.Println("err a : ", err)
	// 	return
	// }
	// ptime := time.Now()
	// fmt.Println(gettime)
	// if gettime.Cnt == 0 {
	// 	postgresQuerier.InsertSMSProcess(ctcx)
	// } else {
	// 	ptime, err = time.Parse("2006-01-02 15:04:05.000000+00", gettime.Valueskeys.(string))
	// }
	// if ptime.After(time.Now()) {
	// 	fmt.Println("ptime : ", ptime)
	// 	fmt.Println(ptime, " : ", time.Now())
	// 	return
	// } else {
	// 	fmt.Println("ctime : ", time.Now())
	// }
	// fmt.Println(ptime, " : ", time.Now())
	// smsdata, err := postgresQuerier.SelectSMSData(ctcx)
	// for keys, values := range smsdata {
	// 	fmt.Println(keys)
	// 	fmt.Println(values)
	// 	err = SendMail(*values)
	// 	if err != nil {
	// 		fmt.Println("err a : ", err)
	// 	} else {
	// 		err = postgresQuerier.UpdateSMS(ctcx, values.SmsID)
	// 	}
	// 	err = postgresQuerier.UpdateProcessTime(ctcx)
	// }
	return
}

func SendMail(email gen.SmsStatus) error {
	// Set up authentication information.
	auth := smtp.PlainAuth("", "support@chokumin.com", "Ryuta7233#", "mail50.onamae.ne.jp")

	smtpServer := "mail50.onamae.ne.jp"
	smtpPort := 587

	from := "support@chokumin.com"

	add := fmt.Sprintf("%s:%d", smtpServer, smtpPort)

	// Construct the email message with proper headers.
	msg := fmt.Sprintf("From: %s\r\n", from)
	msg += fmt.Sprintf("To: %s\r\n", email.Email)
	msg += fmt.Sprintf("Subject: %s\r\n\r\n%s", email.EmailSubject, email.Body)

	to := []string{email.Email}

	err := smtp.SendMail(add, auth, from, to, []byte(msg))
	if err != nil {
		fmt.Println("Error sending email:", err)
		return err
	}
	fmt.Println("Mail Sent")

	return nil
}
