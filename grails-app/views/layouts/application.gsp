<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
  		<asset:stylesheet src="application.css"/>
  		<asset:javascript src="application.js"/>	
		<g:layoutHead/>
	</head>
	<body class="${session?.empresa?.rfc=='IMP921016J16'?'skin-3':''}">
		<div id="wrapper">
			<nav class="navbar-default navbar-static-side" role="navigation">
				<div class="sidebar-collapse">
					<g:render template="/_menu/sidebar"/>
				</div>
			</nav>
			<div id="page-wrapper" class="gray-bg dashbard-1">
				<div class="row border-bottom">
					<nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
						<div class="navbar-header">
							<a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#">
								<i class="fa fa-bars"></i> 
							</a>
						</div>
						
						
						%{-- <g:render template="/_menu/main/navbar-right"/> --}%
					</nav>
				</div>
				<g:layoutBody/>
				<div class="row">
					<div class="col-lg-12">
						<div class="wrapper wrapper-content">
							%{-- <g:render template="/_menu/main/footer"/> --}%
						</div>
					</div>
				</div>
			</div>
			%{-- <g:render template="/_menu/main/right-sidebar"/>  --}%
		</div>
		<!-- Main navigation
        ===============================================-->

		
	
	</body>
</html>
