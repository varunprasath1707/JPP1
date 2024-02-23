--
-- PostgreSQL database dump
--

-- Dumped from database version 13.13
-- Dumped by pg_dump version 13.13 (Debian 13.13-0+deb11u1)

-- Started on 2023-12-23 07:24:16 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 16411)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 4125 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 724 (class 1247 OID 16869)
-- Name: comment_status; Type: TYPE; Schema: public; Owner: dev
--

CREATE TYPE public.comment_status AS ENUM (
    'Liked',
    'Disliked',
    'Report'
);


ALTER TYPE public.comment_status OWNER TO dev;

--
-- TOC entry 703 (class 1247 OID 16553)
-- Name: select_choice; Type: TYPE; Schema: public; Owner: dev
--

CREATE TYPE public.select_choice AS ENUM (
    'tokyo',
    'kyoto',
    'other',
    'choice_one',
    'choice_two',
    'choice_three',
    'choice_four',
    'choice_five',
    'choice_six'
);


ALTER TYPE public.select_choice OWNER TO dev;

--
-- TOC entry 681 (class 1247 OID 16455)
-- Name: select_gender; Type: TYPE; Schema: public; Owner: dev
--

CREATE TYPE public.select_gender AS ENUM (
    'male',
    'female',
    'other'
);


ALTER TYPE public.select_gender OWNER TO dev;

--
-- TOC entry 687 (class 1247 OID 16470)
-- Name: select_location; Type: TYPE; Schema: public; Owner: dev
--

CREATE TYPE public.select_location AS ENUM (
    'tokyo',
    'kyoto',
    'other'
);


ALTER TYPE public.select_location OWNER TO dev;

--
-- TOC entry 696 (class 1247 OID 16530)
-- Name: select_locations; Type: TYPE; Schema: public; Owner: dev
--

CREATE TYPE public.select_locations AS ENUM (
    'tokyo',
    'kyoto',
    'other'
);


ALTER TYPE public.select_locations OWNER TO dev;

SET default_table_access_method = heap;

--
-- TOC entry 204 (class 1259 OID 16537)
-- Name: agenda_master; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_master (
    agenda_master_id bigint NOT NULL,
    user_id bigint NOT NULL,
    topic_name character varying(45) DEFAULT ''::character varying NOT NULL,
    location_level public.select_locations NOT NULL,
    discussion_details text DEFAULT ''::character varying NOT NULL,
    is_approved boolean DEFAULT false NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.agenda_master OWNER TO dev;

--
-- TOC entry 211 (class 1259 OID 16856)
-- Name: agenda_master_id_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.agenda_master_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.agenda_master_id_seq OWNER TO dev;

--
-- TOC entry 4126 (class 0 OID 0)
-- Dependencies: 211
-- Name: agenda_master_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.agenda_master_id_seq OWNED BY public.agenda_master.agenda_master_id;


--
-- TOC entry 201 (class 1259 OID 16461)
-- Name: users; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    first_name character varying(45) DEFAULT ''::character varying NOT NULL,
    last_name character varying(45) DEFAULT ''::character varying NOT NULL,
    handle_name character varying(40) DEFAULT ''::character varying NOT NULL,
    display_name character varying(40) DEFAULT ''::character varying NOT NULL,
    email_id character varying(45) DEFAULT ''::character varying NOT NULL,
    token character varying(500) DEFAULT ''::character varying NOT NULL,
    gender public.select_gender NOT NULL,
    dob date DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_address character varying(100) DEFAULT ''::character varying NOT NULL,
    ph_fixed bigint DEFAULT 0 NOT NULL,
    ph_mobile bigint DEFAULT 0 NOT NULL,
    jpp_password character varying(50) DEFAULT ''::character varying NOT NULL,
    otp integer DEFAULT 0 NOT NULL,
    is_admin boolean DEFAULT false NOT NULL,
    mobile_verification boolean DEFAULT false NOT NULL,
    is_delete boolean DEFAULT false NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    email_verification boolean DEFAULT false NOT NULL,
    otp_email integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.users OWNER TO dev;

--
-- TOC entry 209 (class 1259 OID 16647)
-- Name: users_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_seq OWNER TO dev;

--
-- TOC entry 4127 (class 0 OID 0)
-- Dependencies: 209
-- Name: users_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.users_seq OWNED BY public.users.user_id;


--
-- TOC entry 208 (class 1259 OID 16608)
-- Name: agenda_acceptence; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_acceptence (
    agenda_acceptence_id bigint NOT NULL,
    user_id bigint DEFAULT nextval('public.users_seq'::regclass) NOT NULL,
    agenda_master_id bigint DEFAULT nextval('public.agenda_master_id_seq'::regclass) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.agenda_acceptence OWNER TO dev;

--
-- TOC entry 205 (class 1259 OID 16559)
-- Name: agenda_choice; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_choice (
    agenda_choice_id bigint NOT NULL,
    agenda_master_id bigint DEFAULT nextval('public.agenda_master_id_seq'::regclass) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    choice_text character varying DEFAULT ''::character varying NOT NULL,
    display_order integer DEFAULT 0 NOT NULL,
    choice_id character varying DEFAULT ''::character varying NOT NULL COLLATE pg_catalog."C"
);
ALTER TABLE ONLY public.agenda_choice ALTER COLUMN choice_id SET STORAGE PLAIN;


ALTER TABLE public.agenda_choice OWNER TO dev;

--
-- TOC entry 212 (class 1259 OID 16858)
-- Name: agenda_choice_id_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.agenda_choice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.agenda_choice_id_seq OWNER TO dev;

--
-- TOC entry 4128 (class 0 OID 0)
-- Dependencies: 212
-- Name: agenda_choice_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.agenda_choice_id_seq OWNED BY public.agenda_choice.agenda_choice_id;


--
-- TOC entry 207 (class 1259 OID 16588)
-- Name: agenda_comment; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_comment (
    agenda_comment_id bigint NOT NULL,
    user_id bigint DEFAULT nextval('public.users_seq'::regclass) NOT NULL,
    agenda_master_id bigint DEFAULT nextval('public.agenda_master_id_seq'::regclass) NOT NULL,
    comment_text text DEFAULT ''::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.agenda_comment OWNER TO dev;

--
-- TOC entry 213 (class 1259 OID 16862)
-- Name: agenda_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.agenda_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.agenda_comment_id_seq OWNER TO dev;

--
-- TOC entry 4129 (class 0 OID 0)
-- Dependencies: 213
-- Name: agenda_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.agenda_comment_id_seq OWNED BY public.agenda_comment.agenda_comment_id;


--
-- TOC entry 214 (class 1259 OID 16865)
-- Name: seq_agenda_comment_reportlikedislike; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.seq_agenda_comment_reportlikedislike
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_agenda_comment_reportlikedislike OWNER TO dev;

--
-- TOC entry 215 (class 1259 OID 16875)
-- Name: agenda_comment_reportlikedislike; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_comment_reportlikedislike (
    agenda_comment_rll bigint DEFAULT nextval('public.seq_agenda_comment_reportlikedislike'::regclass) NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    agenda_comment_id bigint DEFAULT 0 NOT NULL,
    comment_response public.comment_status DEFAULT 'Liked'::public.comment_status NOT NULL
);


ALTER TABLE public.agenda_comment_reportlikedislike OWNER TO dev;

--
-- TOC entry 206 (class 1259 OID 16571)
-- Name: agenda_vote; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.agenda_vote (
    agenda_vote_id bigint NOT NULL,
    user_id bigint NOT NULL,
    agenda_master_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.agenda_vote OWNER TO dev;

--
-- TOC entry 217 (class 1259 OID 17705)
-- Name: agenda_vote_id_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.agenda_vote_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE public.agenda_vote_id_seq OWNER TO dev;

--
-- TOC entry 4130 (class 0 OID 0)
-- Dependencies: 217
-- Name: agenda_vote_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.agenda_vote_id_seq OWNED BY public.agenda_vote.agenda_vote_id;


--
-- TOC entry 219 (class 1259 OID 17748)
-- Name: notice_master; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.notice_master (
    srno bigint DEFAULT 1 NOT NULL,
    notice_text character varying DEFAULT ''::character varying NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.notice_master OWNER TO dev;

--
-- TOC entry 202 (class 1259 OID 16477)
-- Name: petition_master; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.petition_master (
    petition_master_id bigint NOT NULL,
    user_id bigint NOT NULL,
    title character varying(50) DEFAULT ''::character varying NOT NULL,
    locationlevel public.select_location NOT NULL,
    user_target character varying(45) DEFAULT ''::character varying NOT NULL,
    content character varying(45) DEFAULT ''::character varying NOT NULL,
    submission_address character varying(50) DEFAULT ''::character varying NOT NULL,
    deadline date DEFAULT CURRENT_DATE NOT NULL,
    recruitmentcomments character varying(100) DEFAULT ''::character varying NOT NULL,
    is_deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.petition_master OWNER TO dev;

--
-- TOC entry 210 (class 1259 OID 16853)
-- Name: petition_master_seq; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.petition_master_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483647
    CACHE 1;


ALTER TABLE public.petition_master_seq OWNER TO dev;

--
-- TOC entry 4131 (class 0 OID 0)
-- Dependencies: 210
-- Name: petition_master_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.petition_master_seq OWNED BY public.petition_master.petition_master_id;


--
-- TOC entry 216 (class 1259 OID 17703)
-- Name: seq_petition_acceptence; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.seq_petition_acceptence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_petition_acceptence OWNER TO dev;

--
-- TOC entry 4132 (class 0 OID 0)
-- Dependencies: 216
-- Name: seq_petition_acceptence; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.seq_petition_acceptence OWNED BY public.agenda_acceptence.agenda_acceptence_id;


--
-- TOC entry 203 (class 1259 OID 16490)
-- Name: petition_acceptence; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.petition_acceptence (
    petition_acceptence_id bigint DEFAULT nextval('public.seq_petition_acceptence'::regclass) NOT NULL,
    petition_master_id bigint DEFAULT nextval('public.petition_master_seq'::regclass) NOT NULL,
    user_id bigint DEFAULT nextval('public.users_seq'::regclass) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.petition_acceptence OWNER TO dev;

--
-- TOC entry 218 (class 1259 OID 17711)
-- Name: seq_agenda_acceptence; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.seq_agenda_acceptence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_agenda_acceptence OWNER TO dev;

--
-- TOC entry 4133 (class 0 OID 0)
-- Dependencies: 218
-- Name: seq_agenda_acceptence; Type: SEQUENCE OWNED BY; Schema: public; Owner: dev
--

ALTER SEQUENCE public.seq_agenda_acceptence OWNED BY public.agenda_acceptence.agenda_acceptence_id;


--
-- TOC entry 222 (class 1259 OID 17771)
-- Name: seq_choice_master_id; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.seq_choice_master_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 99999999
    CACHE 1;


ALTER TABLE public.seq_choice_master_id OWNER TO dev;

--
-- TOC entry 220 (class 1259 OID 17760)
-- Name: seq_unique_visitor; Type: SEQUENCE; Schema: public; Owner: dev
--

CREATE SEQUENCE public.seq_unique_visitor
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 9999999999
    CACHE 1;


ALTER TABLE public.seq_unique_visitor OWNER TO dev;

--
-- TOC entry 221 (class 1259 OID 17765)
-- Name: unique_visitor; Type: TABLE; Schema: public; Owner: dev
--

CREATE TABLE public.unique_visitor (
    uv_id bigint DEFAULT nextval('public.seq_unique_visitor'::regclass) NOT NULL,
    vistor_ip character varying(100) DEFAULT ''::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.unique_visitor OWNER TO dev;

--
-- TOC entry 3945 (class 2604 OID 17713)
-- Name: agenda_acceptence agenda_acceptence_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_acceptence ALTER COLUMN agenda_acceptence_id SET DEFAULT nextval('public.seq_agenda_acceptence'::regclass);


--
-- TOC entry 3929 (class 2604 OID 16861)
-- Name: agenda_choice agenda_choice_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_choice ALTER COLUMN agenda_choice_id SET DEFAULT nextval('public.agenda_choice_id_seq'::regclass);


--
-- TOC entry 3939 (class 2604 OID 16864)
-- Name: agenda_comment agenda_comment_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_comment ALTER COLUMN agenda_comment_id SET DEFAULT nextval('public.agenda_comment_id_seq'::regclass);


--
-- TOC entry 3924 (class 2604 OID 16860)
-- Name: agenda_master agenda_master_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_master ALTER COLUMN agenda_master_id SET DEFAULT nextval('public.agenda_master_id_seq'::regclass);


--
-- TOC entry 3936 (class 2604 OID 17707)
-- Name: agenda_vote agenda_vote_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_vote ALTER COLUMN agenda_vote_id SET DEFAULT nextval('public.agenda_vote_id_seq'::regclass);


--
-- TOC entry 3909 (class 2604 OID 16855)
-- Name: petition_master petition_master_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_master ALTER COLUMN petition_master_id SET DEFAULT nextval('public.petition_master_seq'::regclass);


--
-- TOC entry 3894 (class 2604 OID 17644)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_seq'::regclass);


--
-- TOC entry 3978 (class 2606 OID 17759)
-- Name: notice_master Notice_master_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.notice_master
    ADD CONSTRAINT "Notice_master_pkey" PRIMARY KEY (srno);


--
-- TOC entry 3974 (class 2606 OID 16614)
-- Name: agenda_acceptence agenda_acceptence_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_acceptence
    ADD CONSTRAINT agenda_acceptence_pkey PRIMARY KEY (agenda_acceptence_id);


--
-- TOC entry 3968 (class 2606 OID 16565)
-- Name: agenda_choice agenda_choice_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_choice
    ADD CONSTRAINT agenda_choice_pkey PRIMARY KEY (agenda_choice_id);


--
-- TOC entry 3976 (class 2606 OID 16883)
-- Name: agenda_comment_reportlikedislike agenda_comment_reportlikedislike_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_comment_reportlikedislike
    ADD CONSTRAINT agenda_comment_reportlikedislike_pkey PRIMARY KEY (agenda_comment_rll);


--
-- TOC entry 3966 (class 2606 OID 16546)
-- Name: agenda_master agenda_master_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_master
    ADD CONSTRAINT agenda_master_pkey PRIMARY KEY (agenda_master_id);


--
-- TOC entry 3970 (class 2606 OID 16577)
-- Name: agenda_vote agenda_vote_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_vote
    ADD CONSTRAINT agenda_vote_pkey PRIMARY KEY (agenda_vote_id);


--
-- TOC entry 3972 (class 2606 OID 16597)
-- Name: agenda_comment agendacomment_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_comment
    ADD CONSTRAINT agendacomment_pkey PRIMARY KEY (agenda_comment_id);


--
-- TOC entry 3964 (class 2606 OID 16496)
-- Name: petition_acceptence petition_acceptence_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_acceptence
    ADD CONSTRAINT petition_acceptence_pkey PRIMARY KEY (petition_acceptence_id);


--
-- TOC entry 3962 (class 2606 OID 16484)
-- Name: petition_master petition_master_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_master
    ADD CONSTRAINT petition_master_pkey PRIMARY KEY (petition_master_id);


--
-- TOC entry 3960 (class 2606 OID 17646)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 3989 (class 2606 OID 16620)
-- Name: agenda_acceptence agenda_acceptence_agenda_master_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_acceptence
    ADD CONSTRAINT agenda_acceptence_agenda_master_id_fkey FOREIGN KEY (agenda_master_id) REFERENCES public.agenda_master(agenda_master_id);


--
-- TOC entry 3988 (class 2606 OID 17672)
-- Name: agenda_acceptence agenda_acceptence_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_acceptence
    ADD CONSTRAINT agenda_acceptence_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- TOC entry 3983 (class 2606 OID 16566)
-- Name: agenda_choice agenda_choice_agenda_master_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_choice
    ADD CONSTRAINT agenda_choice_agenda_master_id_fkey FOREIGN KEY (agenda_master_id) REFERENCES public.agenda_master(agenda_master_id);


--
-- TOC entry 3982 (class 2606 OID 17657)
-- Name: agenda_master agenda_master_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_master
    ADD CONSTRAINT agenda_master_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- TOC entry 3985 (class 2606 OID 16583)
-- Name: agenda_vote agenda_vote_agenda_master_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_vote
    ADD CONSTRAINT agenda_vote_agenda_master_id_fkey FOREIGN KEY (agenda_master_id) REFERENCES public.agenda_master(agenda_master_id);


--
-- TOC entry 3984 (class 2606 OID 17662)
-- Name: agenda_vote agenda_vote_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_vote
    ADD CONSTRAINT agenda_vote_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- TOC entry 3987 (class 2606 OID 16603)
-- Name: agenda_comment agendacomment_agenda_master_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_comment
    ADD CONSTRAINT agendacomment_agenda_master_id_fkey FOREIGN KEY (agenda_master_id) REFERENCES public.agenda_master(agenda_master_id);


--
-- TOC entry 3986 (class 2606 OID 17667)
-- Name: agenda_comment agendacomment_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.agenda_comment
    ADD CONSTRAINT agendacomment_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- TOC entry 3980 (class 2606 OID 16497)
-- Name: petition_acceptence petition_acceptence_petition_master_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_acceptence
    ADD CONSTRAINT petition_acceptence_petition_master_id_fkey FOREIGN KEY (petition_master_id) REFERENCES public.petition_master(petition_master_id);


--
-- TOC entry 3981 (class 2606 OID 17652)
-- Name: petition_acceptence petition_acceptence_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_acceptence
    ADD CONSTRAINT petition_acceptence_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- TOC entry 3979 (class 2606 OID 17647)
-- Name: petition_master petition_master_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dev
--

ALTER TABLE ONLY public.petition_master
    ADD CONSTRAINT petition_master_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


-- Completed on 2023-12-23 07:24:26 UTC

--
-- PostgreSQL database dump complete
--

