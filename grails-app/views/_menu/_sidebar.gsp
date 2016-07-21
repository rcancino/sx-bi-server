<ul class="nav metismenu" id="side-menu">
    <li class="nav-header">
        <div class="dropdown profile-element">
			<span>
                %{-- <asset:image src="demos/IMG_0002.jpg" class="img-circle" absolute="false"/> --}%
                <img src="http://placehold.it/54x54&text=Foto" class="img-circle" absolute="false">
                %{-- <img src="http://www.lorempixum.com/g/400/100/sports" alt="" class="img-circle" absolute="false"> --}%
                %{-- <img src="https://placeimg.com/48/48/people/sepia" class="img-circle" absolute="false"> --}%
                %{-- <img src="http://loremflickr.com/g/48/48/people" class="img-circle" absolute="false"/> --}%
            </span>
            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
        	    <span class="clear">
        	    	<span class="block m-t-xs">
        	    		<strong class="font-bold">
                            ${applicationContext?.springSecurityService?.getCurrentUser()?.nombres}
                        </strong>
        	        </span>
        	    	<span class="text-muted text-xs block">Sistemas <b class="caret"></b>
        	    	</span>
        		</span>
            </a>
            <ul class="dropdown-menu animated fadeInRight m-t-xs">
                <li>
                    <g:link action="edit" controller="perfil">
                        <i class="fa fa-user-secret"></i> Perfil
                    </g:link>
                </li>
                <li><a href="mailbox.html">Mensajes</a></li>
                <li><a href="contacts.html">Notas</a></li>
                <li class="divider"></li>
                <li><a href="login.html">Salir </a></li>
            </ul>
        </div>
        <div class="logo-element">
            LX
        </div>
    </li>



    <li class="${webRequest.controllerName=='home'?'active':''}" >
        <g:link action="homeDashboard" controller="home">
            <i class="fa fa-area-chart"></i> Inicio
        </g:link>
    </li>

    <li class="">

        <a href="index.html">
            <i class="fa fa-tasks" aria-hidden="true"></i> <span class="nav-label">Sistemas</span> <span class="fa arrow"></span>
        </a>
        <ul class="nav nav-second-level collapse">
            <li class="${webRequest.controllerName=='quartz'?'active':''}" >
                <g:link controller="quartz">
                    <i class="fa fa-cogs" aria-hidden="true"></i>Jobs
                </g:link>
            </li>
            <li class="${webRequest.controllerName=='console'?'active':''}" >
                <g:link controller="quartz">
                    <i class="fa fa-wrench" aria-hidden="true"></i>Consola
                </g:link>
            </li>

        </ul>
    </li>



</ul>