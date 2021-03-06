

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" type="image/png" href="${createLink(controller: "login", action: "getFavicon")}"/>
    <asset:stylesheet src="player/player.css"/>
    <asset:javascript src="player/player.js"/>
    <title>Puzzle Hunt</title>
</head>
<body style="background-color: rgb(127, 33, 52)">
    <g:form useToken="true" style="visibility: hidden">
    </g:form>
    <div style="position: fixed; width: 100%; height: 100%; overflow: auto">
        <div id="rootPane" style="margin: auto; width: 100%; padding: 25px 0;">
            <div style="display: table; margin: auto">
                <div id="titlePane" class='greeting' style="display: none">
                </div>
                <div id="statusPane" class="greeting" style="display: none; vertical-align: bottom; cursor: pointer">
                </div>
            </div>
        </div>
    </div>
    <div id="modal" style="visibility: hidden; position: absolute; left: 0px; right: 0px; top: 0px; bottom: 0px; z-index: 100">
        <div id="modal-shade"></div>
        <div id="modal-root"></div>
    </div>
</body>
</html>
