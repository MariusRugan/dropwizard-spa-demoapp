$(document).ready(function() {
    var lock = new Auth0Lock(
      AUTH0_CLIENT_ID,
      AUTH0_DOMAIN,
      {
          assetsUrl: 'https://cdn.eu.auth0.com/',
          cdn: 'https://cdn.eu.auth0.com/' }
    );

    var userProfile;

    $('.btn-login').click(function(e) {
      e.preventDefault();
      lock.showSignin(function(err, profile, token) {
        if (err) {
          // Error callback
          console.log("There was an error");
          alert("There was an error logging in");
        } else {
          // Success calback

          // Save the JWT token.
          localStorage.setItem('userToken', token);

          // Save the profile
          userProfile = profile;

          $('.login-box').hide();
          $('.logged-in-box').show();
          $('.nickname').text(profile.nickname);
          $('.nickname').text(profile.name);
          $('.avatar').attr('src', profile.picture);
        }
      });
    });

    $.ajaxSetup({
      'beforeSend': function(xhr) {
        if (localStorage.getItem('userToken')) {
          xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('userToken'));
        }
      }
    });

    $('.btn-api').click(function(e) {
        console.log(userProfile.name);
        $.ajax({
          url: 'http://localhost:9080/api/hello-world?name=' +  userProfile.name,
          method: 'GET'
        }).then(function(data, textStatus, jqXHR) {
            $('.nickname').text(data.content + ' ' + 'you\'ve called ' + data.id);
        }, function() {
          alert("You need to download the server seed and start it to call this API");
        });
    });


});
