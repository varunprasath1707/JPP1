package com.RecruitmentApplication.service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.*;



@Service
public class GoogleCalendarService {
	
	 @Autowired
	    private JavaMailSender javaMailSender;
	 
	//public String googleMeetLink = "https://meet.google.com/czv-frkz-kmd"; 
	 	 
	 public void googleCalenderService( String candidateemail,String interviewfrom,String interviewto,String candidatename, String meetingLink)  throws Exception {
		 
		 try {
		  
			String eventSummary = "Interview With Zenitus";
	        String eventDescription = "Internal Interview";
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//	        Date startDateTime = sdf.parse(interviewfrom);
//	   	    Date endDateTime = sdf.parse(interviewto);
	        System.out.println(interviewfrom);
	        System.out.println(interviewto);
	        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        dateFormat.setTimeZone(TimeZone.getDefault());
	        Date startDateTime = dateFormat.parse(interviewfrom);
	   	    Date endDateTime = dateFormat.parse(interviewto);
	   	    
	   	    System.out.println(startDateTime);
	   	    System.out.println(endDateTime);  
	   
	      	
	   	// Create a new Calendar
	   	    Calendar calendar = new Calendar();
	   	    
	   	 System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
	   	    
	   	// Create a VTimeZone for your desired time zone
	   	 TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
	   	 TimeZone timezone = registry.getTimeZone("Asia/Kolkata"); // Change to your desired time zone

	   	    
	   	 // Create the event
		    VEvent event = new VEvent(new DateTime(startDateTime), new DateTime(endDateTime), eventSummary);
		   
		 // Set the time zone for the event
		    event.getStartDate().setTimeZone(timezone);
		    ((DtEnd) event.getProperty(Property.DTEND)).setTimeZone(timezone);
		    
		    event.getProperties().add(new Description(eventDescription));
		    
		 // Add event description with Google Meet link
		    String eventDescriptionWithMeetLink = eventDescription + "\nGoogle Meet Link: " + meetingLink ;
		    event.getProperties().add(new Description(eventDescriptionWithMeetLink));
		
		    // add a location of a meeting    
		    Location eventLocation = new Location("Online Interview");
		    event.getProperties().add(eventLocation);
		    
		 // Add attendees
		    Attendee candidateAttendee = new Attendee("mailto:" + candidateemail);
		    candidateAttendee.getParameters().add(Rsvp.TRUE);
		    event.getProperties().add(candidateAttendee);

		    //Attendee interviewerAttendee = new Attendee("mailto:" + intervieweremail);
		    //event.getProperties().add(interviewerAttendee);
		    

		    calendar.getComponents().add(event);
		    

		    // Serialize the iCalendar data
		    String eventICalendarContent = calendar.toString();
		    
		    
		    // Insert the generated HTML content into a <div> on your web page
		    String webPage = "<html><body><div><p>Dear "+candidatename+",<br></p><p>I hope this email finds you well.</p>"
		    		+ "<p>I am writing to invite you to a meeting on ["+startDateTime+"] to discuss [INTERVIEW]. The meeting will be held at [Location] or remotely via [Google Meet].</p>"
		    		+ "<p>If you are unable to attend the meeting, please let me know as soon as possible so that we can reschedule."
		    		+ "I look forward to seeing you at the meeting.</p></div><div id='eventInfo'><div><p><strong>When</strong><br/>Start:</t>" + startDateTime +"<br>End:</t>" + endDateTime  + ""
		    		+ "</br></p><p><strong>GoogleMeetLink:</strong><br>Click This:<a href="+meetingLink+">Meething Link</a></p><p><strong>Organiser</strong><br/>hr@zenitus.com</p><p><strong>Guests</strong><br/>"+ candidateemail +""
		    				+ "</p></div></body></html>";
		    

		    // Create and send the email
		    MimeMessage message = javaMailSender.createMimeMessage();
		    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
		    helper.setTo(candidateemail);
		    //helper.setCc("ksrinathk1@gmail.com");
		    helper.setSubject("Zenitus Interview Invitation");
		    helper.setText("Please find the attached event invitation.",webPage);

		    // Attach the iCalendar file
//		    byte[] icsBytes = getCalendarInvite();
//		    InputStreamSource source = new ByteArrayResource(icsBytes);
//		    helper.addAttachment("event.ics", source);
		    helper.addAttachment("event.ics", new ByteArrayResource(eventICalendarContent.getBytes()), "text/calendar");

		    // Send the email
		    javaMailSender.send(message);
	   	
		 } catch (Exception e) {
			    // Handle exceptions
			    e.printStackTrace();
			}
		 
	 }
	 
	
//	 private byte[] getCalendarInvite() {
//	        String icsContent = "BEGIN:VCALENDAR\n" +
//	                "PRODID:-//Company//Product//EN\n" +
//	                "VERSION:2.0\n" +
//	                "METHOD:REQUEST\n" +
//	                "BEGIN:VEVENT\n" +
//	                "UID:12345\n" +
//	                "DTSTAMP:20230923T120000Z\n" +
//	                "DTSTART:20230923T130000Z\n" +
//	                "DTEND:20230923T140000Z\n" +
//	                "SUMMARY:Meeting\n" +
//	                "DESCRIPTION:Meeting Description\n" +
//	                "LOCATION:Meeting Location\n" +
//	                "END:VEVENT\n" +
//	                "END:VCALENDAR";
//
//	        return icsContent.getBytes(); // Convert to bytes
//	    }
public void googleCalenderDeleteService( String candidateEmail,String interviewfrom,String interviewto,String googleMeetLink,String ReasonforCancelation)  throws Exception {

		 try {
			 
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		        Date startDateTime = sdf.parse(interviewfrom);
		   	    Date endDateTime = sdf.parse(interviewto);
		      	
		   	 // Create the new calendar  	
		   	 Calendar calendar = new Calendar();
		        calendar.getProperties().add(new ProdId("-//My Application//iCal4j 1.0//EN"));
		        calendar.getProperties().add(Version.VERSION_2_0);
		        calendar.getProperties().add(CalScale.GREGORIAN);
		        calendar.getProperties().add(Method.CANCEL);

		        // Create the event
		        VEvent event = new VEvent(new DateTime(startDateTime),new DateTime(endDateTime), "Interview Cancelled");
		        // Use the fully qualified class name for Property from iCal4j
		        Property statusProperty = new XProperty("STATUS", "CANCELLED");
		        event.getProperties().add(statusProperty);
		        // Add the event to the calendar
		        calendar.getComponents().add(event);


		        // Serialize the iCalendar data
		        String eventICalendarContent = calendar.toString();
		        
			    String webPage = "<html><body><div id='eventInfo'><div><p><strong>"+ReasonforCancelation+"</strong></p><p><strong>When</strong><br/>Start:</t>" + startDateTime +"<br>End:</t>" + endDateTime  + "</br></p><p><strong>GoogleMeetLink:</strong><br><a href="+googleMeetLink+">Meeting Link</a></p><p><strong>Organiser</strong><br/>hr@zenitus.com</p><p><strong>Guests</strong><br/>"+ candidateEmail +"</p></div></body></html>";


		        // Create and send the cancellation email
		        MimeMessage message = javaMailSender.createMimeMessage();
		        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
		        helper.setTo(candidateEmail);
		        //helper.setCc("ksrinathk1@gmail.com");
		        helper.setSubject("Event Cancellation Notice");
		        helper.setText("The event has been canceled.",webPage);

		        
		     // Attach the iCalendar file
//			    byte[] icsBytes = getEventCancellation();
//			    InputStreamSource source = new ByteArrayResource(icsBytes);
//			    helper.addAttachment("event.ics", source,"text/calendar;method=CANCEL");
		        helper.addAttachment("event_cancelled.ics", new ByteArrayResource(eventICalendarContent.getBytes()), "text/calendar;method=CANCEL");

		        // Send the cancellation email
		        javaMailSender.send(message);
			
		     }
		 catch (Exception e) {
			// TODO: handle exception
		}
}
//private byte[] getEventCancellation() {
//    String icsContent = "BEGIN:VCALENDAR\n" +
//            "PRODID:-//Company//Product//EN\n" +
//            "VERSION:2.0\n" +
//            "METHOD:CANCEL\n" + // Use METHOD:CANCEL to indicate event cancellation
//            "BEGIN:VEVENT\n" +
//            "UID:12345\n" + // Use the same UID as the event being canceled
//            "DTSTAMP:20230923T120000Z\n" + // Use the same DTSTAMP as the event being canceled
//            "STATUS:CANCELLED\n" + // Indicate that the event has been canceled
//            "SUMMARY:Meeting (Cancelled)\n" + // Add '(Cancelled)' to the summary
//            "DESCRIPTION:Meeting Description\n" + // Provide a description if needed
//            "END:VEVENT\n" +
//            "END:VCALENDAR";
//
//    return icsContent.getBytes(); // Convert to bytes
//}
  
}
