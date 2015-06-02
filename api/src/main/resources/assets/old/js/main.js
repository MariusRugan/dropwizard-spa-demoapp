var token = null;

$(function()
{
	setupEventListeners();
});

function setupEventListeners()
{
	setLoginFormBehaviour();
	setRefreshButtonBehaviour();
	setLogoutButtonBehaviour();
}

function setLoginFormBehaviour()
{
	console.log("setting up login form");
	$("#loginForm").submit(function(event)
	{
		console.log("logging in");
		login();
		event.preventDefault();
	});
}

function setRefreshButtonBehaviour()
{
	$("#refreshTokenButton").click(function(event)
	{
		console.log("refreshing token");
		refreshToken();
		event.preventDefault();
	});
}

function setLogoutButtonBehaviour()
{
		$("#logoutButton").click(function(event)
  	{
  		console.log("refreshing token");
  		logout();
  		event.preventDefault();
  	});
}

function logout()
{
	Api.logout(token, function(token_param)
	{
		console.log("LOGOUT!");
	});
}

function login()
{
	const username = $("#nameInput").val();
	const password = $("#passwordInput").val();
	Api.login(username, password, function(token)
	{
		loggedIn(token);
	});
}

function refreshToken()
{
	Api.refreshToken(token, function(token_param)
	{
		token = token_param;
		updateMainDiv();
	});
}

function loggedIn(token_param)
{
	token = token_param;
	updateMainDiv()
}

function updateMainDiv()
{
	console.log("Token is:");
	console.log(token);
	const decoded = deconstructJWT(token.raw);
	console.log(decoded);
	const username = decoded.user;
	const role = decoded.role;
	$("#usernameSpan").text(username);
	$("#roleSpan").text(role);
	$("#rawTokenSpan").text(token.raw);
	switchToDiv("#mainPageDiv");
}

function switchToDiv(div)
{
	$(".mainDiv").hide();
	$(div).show();
}

// Helper function to extract claims from a JWT. Does *not* verify the
// validity of the token.
// credits: https://github.com/firebase/angularFire/blob/master/angularFire.js#L370
// polyfill window.atob() for IE8: https://github.com/davidchambers/Base64.js
// or really fast Base64 by Fred Palmer: https://code.google.com/p/javascriptbase64/
// taken from https://gist.github.com/katowulf/6231937
function deconstructJWT(token) {
   var segments = token.split(".");
   if (!segments instanceof Array || segments.length !== 3) {
      throw new Error("Invalid JWT");
   }
   var claims = segments[1];
   return JSON.parse(decodeURIComponent(escape(window.atob(claims))));
}