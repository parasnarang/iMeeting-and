package com.thoughtworks.imeeting.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

public class CreateEventTask extends AsyncTask<Object, Integer, Event> {
	private Context context;
	private String calendarId;
	private Calendar service;
	
	public CreateEventTask(Calendar service, Context context) {
		 this( service, context, "primary");
	}
	
	public CreateEventTask(Calendar service, Context context, String calendarId) {
		this.service = service;
		this.context = context;
		this.calendarId = calendarId;
	}
	
	@Override
    protected Event doInBackground(Object... params) {
		Event createdEvent = null;
		try {
			Event event = new Event();

			
			ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
			attendees.add(new EventAttendee().setEmail(calendarId).setResource(true));
			Date startDate = (Date) params[2];
			Date endDate = (Date) params[3];
			DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
			DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
			
			event.setAttendees(attendees);
			event.setSummary((String) params[0]);
			event.setLocation((String) params[1]);
			event.setStart(new EventDateTime().setDateTime(start));			
			event.setEnd(new EventDateTime().setDateTime(end));

			createdEvent = service.events().insert("primary", event).execute();

			System.out.println(createdEvent.getId());
		} catch(GoogleJsonResponseException e){
			e.printStackTrace();
			if(e.getStatusCode() == 401)
				((com.thoughtworks.imeeting.BaseActivity)context).invalidateToken();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return createdEvent;
    }

    protected void onPostExecute(Event event) {	
    	
    }
}
