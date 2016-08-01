<!doctype html>
<html>
    <head>
        <title><g:if env="development">Runtime Exception</g:if><g:else>Error</g:else></title>
        <meta name="layout" content="main">
        <asset:stylesheet src="application.css"/>
        <asset:stylesheet src="luxor.css"/>
    </head>
    <body>
        <div id="wrapper">
            <g:if test="${Throwable.isInstance(exception)}">
                <g:renderException exception="${exception}" />
            </g:if>
            <g:elseif test="${request.getAttribute('javax.servlet.error.exception')}">
                <g:renderException exception="${request.getAttribute('javax.servlet.error.exception')}" />
            </g:elseif>
            <g:else>
                <ul class="errors">
                    <li>An error has occurred</li>
                    <li>Exception: ${exception}</li>
                    <li>Message: ${message}</li>
                    <li>Path: ${path}</li>
                </ul>
            </g:else>

        </div>

    </body>
</html>
