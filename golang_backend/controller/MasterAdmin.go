package controller

import (
	"ZenitusJPP/database/postgres/gen"
	"ZenitusJPP/models"
	service "ZenitusJPP/service"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strings"
)

type MasterAdmin struct {
	MasterUser      service.MasterUser
	UserQuerier     gen.UserQuerier
	PetitionQuerier gen.PetitionQuerier
	AgendaQuerier   gen.AgendaQuerier
}

func (c *MasterAdmin) AdminLogin(resp http.ResponseWriter, req *http.Request) {
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

	// Check if the user is an admin
	if !cnt.IsAdmin {
		err = errors.New("user is not an admin")
		service.WriteResponseErr(resp, http.StatusUnauthorized, err)
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
		Message:      "User Loggedin successfully",
		Token:        token,
		ResponseData: cnt,
	})
}

func (c *MasterAdmin) AdminDashboard(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.UserQuerier.SelectAdminContent(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "Admin Dashboard",
		ResponseData: obj,
	})
}

func (c *MasterAdmin) UserList(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.UserQuerier.SelectUserInfo(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed User list",
		ResponseData: obj,
	})
}

func (c *MasterAdmin) UserDelete(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.UserObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	// Validation
	err = c.UserQuerier.UpdateDeletedByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully deleted user",
	})
}

func (c *MasterAdmin) AgendaList(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.AgendaQuerier.SelectAgendaAllInfoAdmin(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Agenda list",
		ResponseData: obj,
	})
}

func (c *MasterAdmin) ChangeApprovedStatus(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.AgendaObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	err = c.AgendaQuerier.UpdateAgendaApprovedStatus(req.Context(), ReqObj.AgendaMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully updated Agenda Status",
	})
}

func (c *MasterAdmin) AgendaDelete(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.AgendaObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	// Validation
	err = c.AgendaQuerier.UpdateDeletedByAgendaId(req.Context(), ReqObj.AgendaMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully deleted Agenda",
	})
}

func (c *MasterAdmin) PetitionList(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.PetitionQuerier.SelectPetitionInfo(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Petition list",
		ResponseData: obj,
	})
}

func (c *MasterAdmin) PetitionDelete(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PetitionObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	// Validation
	err = c.PetitionQuerier.UpdateDeletedByPetitionId(req.Context(), ReqObj.PetitionMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully deleted Petition",
	})
}

func (c *MasterAdmin) NoticeList(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.UserQuerier.SelectNotice(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Notice",
		ResponseData: obj,
	})
}

func (c *MasterAdmin) NoticeUpdate(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdateNoticeParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check user is admin
	// Validation
	// Validation
	// Check duplicate user
	err = c.UserQuerier.UpdateNotice(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully Updated the Notice Text",
	})
}

func (c *MasterAdmin) AdminLogout(resp http.ResponseWriter, req *http.Request) {
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
		Message: "Admin Logout successfully",
	})
}
