var Api = (function()
{
    var pub = {},
    //Private property
    greyFloorTile = null;

    //Public property
    //pub.ingredient = "Bacon Strips";

    //Public method
    pub.login = function(username, password, callback)
    {
    const parameters = { username: username, password: password};
    console.log("logging in...");
    console.log(parameters);
		$.get("api/token", parameters, function(data)
		{
			callback(data);
		});
    };

    pub.refreshToken = function(token, callback)
    {
    console.log("refreshing token...");
    $.ajax({
        url: "api/token/new",
        type: "GET",
        headers: { 'x-token-jwt': token.raw }
    }).done(function(newToken)
    {
        callback(newToken);
    })
    };

    pub.logout = function(token, callback)
    {
    console.log("logging out...");
    $.ajax({
        url: "api/token",
        type: "DELETE",
        headers: { 'x-token-jwt': token.raw }
    }).done(function()
    {
        callback();
    })
    };

    //Private method
    /*
    function privateWay() {
        console.log("private method");
    }
	*/
    //Return just the public parts
    return pub;

}());