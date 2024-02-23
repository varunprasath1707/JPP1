package router

import (
	"ZenitusJPP/configuration"
	"ZenitusJPP/controller"
	"ZenitusJPP/cron"

	// "ZenitusJPP/controller"
	"ZenitusJPP/database/postgres"
	"ZenitusJPP/middleware"
	service "ZenitusJPP/service"
	"fmt"

	// Middleware "ZenitusJPP/middleware"
	"context"
	"net/http"
	"sync"

	chimiddleware "github.com/go-chi/chi/middleware"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/cors"
	"github.com/jasonlvhit/gocron"
)

type ChiRouter interface {
	InitRouter() (*chi.Mux, func())
}

type router struct {
	cfg configuration.Config
}

func (router *router) InitRouter() (*chi.Mux, func()) {
	ctx := context.Background()
	fmt.Println("actx")
	// projectID := os.Getenv("PROJECT_ID")
	// if projectID == "" {
	// 	log.Fatalf("Failed to load env vars, some were found empty.")
	// }

	postgresQuerier := postgres.Connect(router.cfg)

	loginServitor := service.NewLogin(postgresQuerier, router.cfg)

	/* ------------------------------- controller ------------------------------ */
	MasterControllerObj := controller.MasterUser{
		UserQuerier: postgresQuerier,
	}
	MasterPetitionObj := controller.MasterPetition{
		PetitionQuerier: postgresQuerier,
		UserQuerier:     postgresQuerier,
	}
	MasterAgendaObj := controller.MasterAgenda{
		AgendaQuerier: postgresQuerier,
		UserQuerier:   postgresQuerier,
	}

	MasterAdminObj := controller.MasterAdmin{
		UserQuerier:     postgresQuerier,
		AgendaQuerier:   postgresQuerier,
		PetitionQuerier: postgresQuerier,
	}

	// MasterTeamObj := controller.MasterTeam{
	// 	TeamQuerier: postgresQuerier,
	// }

	// MasterGameObj := controller.MasterGame{
	// 	GameCreateQuerier:  postgresQuerier,
	// 	GameProcessQuerier: postgresQuerier,
	// 	GameMasterQuerier:  postgresQuerier,
	// 	QnsQuerier:         postgresQuerier,
	// }
	/* ------------------------------- MIDDLEWARE ------------------------------- */
	mware := &middleware.Middleware{AuthSrv: loginServitor}
	fmt.Println(mware)
	/* --------------------------------- ROUTER --------------------------------- */

	go StartCronJobs(ctx, postgresQuerier)
	
	r := chi.NewRouter()
	r.Use(chimiddleware.Logger)
	r.Use(cors.Handler(cors.Options{
		// AllowedOrigins:   []string{"https://foo.com"}, // Use this to allow specific origin hosts
		AllowedOrigins: []string{"*", "https://*", "http://*", "http://localhost:9099/*", "http://ec2-3-135-218-183.us-east-2.compute.amazonaws.com:9099/*"},
		// AllowOriginFunc:  func(r *http.Request, origin string) bool { return true },
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type", "X-CSRF-Token"},
		ExposedHeaders:   []string{"Link"},
		AllowCredentials: true,
		MaxAge:           300, // Maximum value not ignored by any of major browsers
	}))
	fmt.Println("actxa")
	r.Group(func(common chi.Router) {
		common.Get("/ping", func(resp http.ResponseWriter, req *http.Request) {
			resp.WriteHeader(http.StatusOK)
			resp.Write([]byte("pong!!!."))
		})

		r.Route("/v1/jpp/user", func(jppapicommon chi.Router) {
			jppapicommon.Post("/registeration", MasterControllerObj.UserRegisteration)
			jppapicommon.Post("/otpmobilecheck", MasterControllerObj.Checkmobileotp)
			jppapicommon.Post("/otpcheck", MasterControllerObj.Checkotp)
			jppapicommon.Post("/forgotpassword", MasterControllerObj.UserForgetPassword)
			jppapicommon.Post("/resendmobileotp", MasterControllerObj.ResendMobileOtp)
			jppapicommon.Post("/newpassword", MasterControllerObj.UserUpdatePassword)
			jppapicommon.Post("/login", MasterControllerObj.Login)
			jppapicommon.Post("/userip", MasterControllerObj.UserIP)
			jppapicommon.Get("/home", MasterAgendaObj.Home)
			jppapicommon.Post("/agenda/list", MasterAgendaObj.AgendaList)
			jppapicommon.Post("/agenda/detail", MasterAgendaObj.AgendaDetail)
			jppapicommon.Post("/petition/list", MasterPetitionObj.PetitionListByLocationLevel)
			jppapicommon.Post("/petition/listbyid", MasterPetitionObj.PetitionListById)
			jppapicommon.Post("/petitionacceptence/listbyid", MasterPetitionObj.PetitionAcceptenceList)
			jppapicommon.Get("/dynamic/list", MasterPetitionObj.DynamicList)
			// Strip payment
			jppapicommon.Post("/createsession", MasterAgendaObj.CreateCheckoutSession)
			jppapicommon.Post("/createdonation", MasterAgendaObj.CreateDonationSession)
			// Success Payment

			jppapicommon.Post("/paymenthistory", MasterAgendaObj.UserPaymentHistory)
			jppapicommon.Post("/processpayment", MasterAgendaObj.PaymentProcess)
			jppapicommon.Post("/paymentreturnurl", MasterAgendaObj.PaymentReturn)
			// Success Payment
			jppapicommon.Post("/donationhistory", MasterAgendaObj.DonationHistory)
			jppapicommon.Post("/processdonation", MasterAgendaObj.DonationProcess)
			jppapicommon.Post("/donationreturnurl", MasterAgendaObj.DonationReturn)

			//sms
			jppapicommon.Get("/sms", MasterControllerObj.SMS)
		})

		r.Route("/v1/jpp", func(jppapi chi.Router) {
			jppapi.Use(mware.AuthenticateUser)
			jppapi.Post("/logout", MasterControllerObj.Logout)
			jppapi.Post("/agenda/add", MasterAgendaObj.CreateAgenda)
			jppapi.Post("/agenda/acceptance/add", MasterAgendaObj.AgendaAcceptance)
			jppapi.Post("/agenda/comment/add", MasterAgendaObj.AgendaComment)
			jppapi.Post("/agenda/comment/reply", MasterAgendaObj.AgendaCommentReply)
			jppapi.Post("/commentreply/list", MasterAgendaObj.AgendaReplyList)
			jppapi.Post("/agenda/choice", MasterAgendaObj.AgendaChoice)
			jppapi.Post("/agendavote", MasterAgendaObj.AgendaVote)
			jppapi.Post("/voteCount", MasterAgendaObj.AgendaVote)
			jppapi.Post("/commentlike", MasterAgendaObj.CommentAgendaLike)
			jppapi.Post("/commentdislike", MasterAgendaObj.CommentAgendaDisLike)
			jppapi.Post("/commentreport", MasterAgendaObj.CommentAgendaReport)
			// jppapi.Get("/agenda/preacceptance/list", MasterAgendaObj.AgendaPreAproved)
			jppapi.Post("/partyinquiry", MasterAgendaObj.PartyInquiry)
			jppapi.Post("/userprofile", MasterControllerObj.UserProfile)
			jppapi.Post("/userprofileupdate", MasterControllerObj.UserProfileUpdate)
			jppapi.Post("/userpasswordchange", MasterControllerObj.UserChangePassword)
			jppapi.Post("/notification", MasterAgendaObj.Notification)
			jppapi.Post("/notification/delete", MasterAgendaObj.NotificationDelete)
			//petition api's
			jppapi.Post("/createpetition", MasterPetitionObj.CreatePetition)
			jppapi.Post("/petition/editbyid", MasterPetitionObj.PetitionEdit)
			jppapi.Post("/petition/signed", MasterPetitionObj.PetitionSign)
			jppapi.Post("/petition/sendmail", MasterPetitionObj.SendPetitionMail)
			//payment

			// jppapi.Post("/paymentadd", MasterControllerObj.PaymentAdd)

			// jppapi.Post("/donationadd", MasterControllerObj.DonationAdd)
		})

		//Admin Router
		r.Route("/v1/jpp/admin", func(jppapicommonadmin chi.Router) {
			jppapicommonadmin.Post("/login", MasterAdminObj.AdminLogin)
			jppapicommonadmin.Get("/dashboard", MasterAdminObj.AdminDashboard)
		})

		r.Route("/v1/jpps", func(jppapiadmin chi.Router) {
			jppapiadmin.Use(mware.AuthenticateUser)
			jppapiadmin.Post("/logout", MasterAdminObj.AdminLogout)
			jppapiadmin.Get("/dashboard", MasterAdminObj.AdminDashboard)
			jppapiadmin.Get("/userlist", MasterAdminObj.UserList)
			jppapiadmin.Post("/user/delete", MasterAdminObj.UserDelete)
			jppapiadmin.Get("/agendalist", MasterAdminObj.AgendaList)
			jppapiadmin.Post("/change/approvedstatus", MasterAdminObj.ChangeApprovedStatus)
			jppapiadmin.Post("/agenda/delete", MasterAdminObj.AgendaDelete)
			jppapiadmin.Get("/petitionlist", MasterAdminObj.PetitionList)
			jppapiadmin.Post("/petition/delete", MasterAdminObj.PetitionDelete)
			jppapiadmin.Get("/notice/list", MasterAdminObj.NoticeList)
			jppapiadmin.Post("/notice/update", MasterAdminObj.NoticeUpdate)

		})
	})
	fmt.Println("actxb")
	return r, func() {
		gocron.Clear()
	}
}

var (
	m          *router
	routerOnce sync.Once
)

// NewChiRouter defines a Singleton, ensuring only a single ChiRouter is created.
func NewChiRouter(cfg configuration.Config) ChiRouter {
	if m == nil {
		routerOnce.Do(func() {
			m = &router{
				cfg: cfg,
			}
		})
	}
	return m
}

func StartCronJobs(ctx context.Context, postgresQuerier postgres.Querier) {
	pro := gocron.NewScheduler()
	// pro.Every(1).Minute().Do(cron.CronDemo, ctx, postgresQuerier)
	// pro.Start()

	pro.Every(1).Minute().Do(cron.CronUpdateLoginStatus, ctx, postgresQuerier)
	pro.Every(1).Day().At("00:00:00").Do(cron.CronYealySubscribtion, ctx, postgresQuerier)
	pro.Every(1).Minute().Do(cron.HourlySMS, ctx, postgresQuerier)
	pro.Start()
}

func funccode(resp http.ResponseWriter, req *http.Request) {
	setupResponse(&resp, req)
}

func setupResponse(w *http.ResponseWriter, req *http.Request) {
	(*w).Header().Set("Access-Control-Allow-Origin", "*")
	(*w).Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
	(*w).Header().Set("Access-Control-Allow-Headers", "access-control-allow-origin, Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorisation, Authorization")
	(*w).Header().Set("Content-Type", "application/json")
	(*w).Header().Set("Access-Control-Expose-Headers", "Authorization")
}
