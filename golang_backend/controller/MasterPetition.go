package controller

import (
	"ZenitusJPP/database/postgres/gen"
	"ZenitusJPP/models"
	service "ZenitusJPP/service"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
)

type MasterPetition struct {
	MasterUser      service.MasterUser
	PetitionQuerier gen.PetitionQuerier
	UserQuerier     gen.UserQuerier
}

func (c *MasterPetition) CreatePetition(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertPetitionInfoParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// validation
	if service.CheckBlankParameter(string(ReqObj.Title)) {
		err := errors.New("topic name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("userId Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Locationlevel)) {
		err := errors.New("locationlevel Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.PetitionContent)) {
		err := errors.New("content Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.SubmissionAddress)) {
		err := errors.New("SubmissionAddress Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.UserTarget)) {
		err := errors.New("target Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Recruitmentcomments)) {
		err := errors.New("recruitment comments Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting user's data by petition id
	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if !MessageObj.MobileVerification {
		err = errors.New("mobile number is not verified")
		service.WriteResponseErr(resp, http.StatusUnauthorized, err)
		return
	}

	petitionID, err := c.PetitionQuerier.InsertPetitionInfo(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Petition Created successfully",
	})

	_, err = c.PetitionQuerier.InsertPetitionSign(req.Context(), gen.InsertPetitionSignParams{
		UserID:           ReqObj.UserID,
		PetitionMasterID: petitionID,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

}

func (c *MasterPetition) DynamicList(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.UserQuerier.SelectDynamicData(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully data code",
		ResponseData: obj,
	})
}

func (c *MasterPetition) PetitionEdit(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.UpdatePetitionInfoByIDParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// validation
	if service.CheckBlankParameter(string(ReqObj.Title)) {
		err := errors.New("topic name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.PetitionMasterID))) {
		err := errors.New("petition Id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Locationlevel)) {
		err := errors.New("locationlevel Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.PetitionContent)) {
		err := errors.New("content Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.SubmissionAddress)) {
		err := errors.New("SubmissionAddress Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.UserTarget)) {
		err := errors.New("target Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Recruitmentcomments)) {
		err := errors.New("recruitment comments Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	err = c.PetitionQuerier.UpdatePetitionInfoByID(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully updated petition",
	})
}

func (c *MasterPetition) PetitionSign(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertPetitionSignParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.PetitionMasterID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check if user already liked
	userLikedCount, err := c.PetitionQuerier.SelectCountpetitionacceptancebyUserIDPetitionAcceptanceID(req.Context(), gen.SelectCountpetitionacceptancebyUserIDPetitionAcceptanceIDParams{UserID: ReqObj.UserID, PetitionMasterID: ReqObj.PetitionMasterID})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userLikedCount > 0 {
		err := errors.New("user has already signed the petition")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.PetitionQuerier.InsertPetitionSign(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Petition Signed successfully",
	})
}

func (c *MasterPetition) PetitionListById(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PetitionObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.PetitionMasterID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.PetitionQuerier.SelectPetitionInfoByPetitionId(req.Context(), ReqObj.PetitionMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Petition By ID",
		ResponseData: MessageObj,
	})
}

func (c *MasterPetition) PetitionAcceptenceList(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PetitionObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.PetitionMasterID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.PetitionQuerier.SelectAllUseracceptenceByPetitionID(req.Context(), ReqObj.PetitionMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Petition By ID",
		ResponseData: MessageObj,
	})
}

func (c *MasterPetition) SendPetitionMail(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PetitionStruct
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.PetitionMasterID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.Email)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//data from petition data
	MessageObj, err := c.PetitionQuerier.SelectPetitionInfoByPetitionId(req.Context(), ReqObj.PetitionMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Retrieve petition acceptence data
	obj, err := c.PetitionQuerier.SelectAllUseracceptenceByPetitionID(req.Context(), ReqObj.PetitionMasterID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	fmt.Println("----------------------------------------------------------------")
	// Print the retrieved data (optional for debugging)
	fmt.Println("Petition Data:", obj)

	// Convert obj to the expected type
	var petitionStructs []gen.SelectAllUseracceptenceByPetitionIDRow
	for _, rowPtr := range obj {
		// Dereference the pointer to get the actual row data
		row := *rowPtr

		// Convert to gen.PetitionMaster
		convertedStruct := gen.SelectAllUseracceptenceByPetitionIDRow{
			FirstName:   row.FirstName,
			LastName:    row.LastName,
			UserAddress: row.UserAddress,
			CreatedOn:   row.CreatedOn,
			// Add field if any needed
		}
		petitionStructs = append(petitionStructs, convertedStruct)
	}

	// Convert obj2 to the expected type
	var petitionInfo []gen.SelectPetitionInfoByPetitionIdRow
	for _, rowPtr := range MessageObj {
		// Dereference the pointer to get the actual row data
		row := *rowPtr

		// // Validate if the deadline has passed
		// if time.Now().Before(row.Deadline) {
		// 	err := errors.New("petition deadline has not passed yet, email can only be sent after the deadline")
		// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
		// 	return
		// }

		// Convert to gen.PetitionMaster
		convertedStruct := gen.SelectPetitionInfoByPetitionIdRow{
			Title:               row.Title,
			UserTarget:          row.UserTarget,
			Locationlevel:       row.Locationlevel,
			PetitionContent:     row.PetitionContent,
			SubmissionAddress:   row.SubmissionAddress,
			Deadline:            row.Deadline,
			Recruitmentcomments: row.Recruitmentcomments,
			HandleName:          row.HandleName,
			DisplayName:         row.DisplayName,
			// Add field if any needed
		}
		// Append to the new slice
		petitionInfo = append(petitionInfo, convertedStruct)
	}

	fmt.Println("----------------------------------------------------------------")
	// Print the converted data (optional for debugging)
	fmt.Println("Converted Petition Data:", petitionStructs)

	fmt.Println("------------------------------------------------------------")
	// Print the converted data (optional for debugging)
	fmt.Println("Converted PetitionInfo Data:", petitionInfo)

	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj4, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj5, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "admin-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if the user has already sent a mail for this petition
	mailCount, err := c.PetitionQuerier.CheckPetitionMailCount(req.Context(), gen.CheckPetitionMailCountParams{
		PetitionMasterID: ReqObj.PetitionMasterID,
		EmailID:          ReqObj.Email,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// If mailCount is greater than 0, the user has already sent a mail for this petition
	if mailCount > 0 {
		err := errors.New("user has already sent a mail for this petition")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting user data by email id
	UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.Email)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.PetitionQuerier.InsertPetitionMailInfo(req.Context(), gen.InsertPetitionMailInfoParams{
		PetitionMasterID: ReqObj.PetitionMasterID,
		EmailID:          ReqObj.Email,
		UserID:           UserObj.UserID,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Send email with the petition list data
	email := ReqObj.Email

	err = service.SendPetitionListEmail(email, ReqObj.CommentText, petitionStructs, petitionInfo, obj1[0], obj2[0], obj3[0], obj4[0], obj5[0])
	if err != nil {
		// Handle the error (e.g., log it)
		fmt.Println("Error sending email:", err)
	}

	// Respond to the client
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "Successfully listed Petition list and sent email",
		ResponseData: petitionStructs, // Optional: Include the converted data in the response
	})
}

func (c *MasterPetition) PetitionListByLocationLevel(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PetitionFilterObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.Petitionfilter))) {
		err := errors.New("petition Filter id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	var ReqObjObj gen.SelectPetitionListParams
	if ReqObj.Petitionfilter == 0 {
		ReqObjObj.Locationlevel = "tokyo"
		ReqObjObj.Locationlevel_2 = "kyoto"
		ReqObjObj.Locationlevel_3 = "other"
	} else if ReqObj.Petitionfilter == 1 {
		ReqObjObj.Locationlevel = "tokyo"
		ReqObjObj.Locationlevel_2 = "tokyo"
		ReqObjObj.Locationlevel_3 = "tokyo"
	} else if ReqObj.Petitionfilter == 2 {
		ReqObjObj.Locationlevel = "kyoto"
		ReqObjObj.Locationlevel_2 = "kyoto"
		ReqObjObj.Locationlevel_3 = "kyoto"
	} else if ReqObj.Petitionfilter == 3 {
		ReqObjObj.Locationlevel = "other"
		ReqObjObj.Locationlevel_2 = "other"
		ReqObjObj.Locationlevel_3 = "other"
	}
	obj, err := c.PetitionQuerier.SelectPetitionList(req.Context(), ReqObjObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Petition list",
		ResponseData: obj,
	})
}
