
# Android Background Service

*WARNING*: This is a quick and dirty hack job.

This app will initialize the splunk/otel sdk in `BackgrounderApp.onCreate()`.
It will display the session ID in a text field in the first fragment.

There are two buttons -- one for starting a service in the background
and one for initiating an instrumented HTTP request. The http request will
just log its info and cause telemetry spans to be created and sent to ingest.

The background service will essentially do the same http request, every few seconds, 
in the background. It should send data with the same session.

The app is also capable of receiving push notifications from Firebase.
It just logs the title and message of the push notification. One can further 
experiment/test by closing the app and causing a push notification to 
launch the app when touched.