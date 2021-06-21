# Example app using sttp-oauth2

Example application using [sttp-oauth2](https://ocadotechnology.github.io/sttp-oauth2/) implemented with [tapir](https://github.com/softwaremill/tapir/), [cats](https://typelevel.org/cats/) and [cats-effect](https://typelevel.org/cats-effect/). It demonstrates authorization code grant fow.


## How it works

This application exposes two endpoints:

 - http://localhost:8080/api/login-redirect - redirects user to Github login page for authorization
 - http://localhost:8080/api/post-login - after login, user is redirected to this endpoint. Application exchanges received authorization code for a token, and uses it to fetch user info from Github API.

## Usage

### Prerequisites

Before starting this application, you need to register an application in Github, to enable OAuth2 authroization. Follow [Github documentation](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app) for instructions on how to register an application.

### Launching

When you have registered your application, launch `sbt` providing application ID and secret by env variales:

```
APP_ID="your-application-id" APP_SECRET="your-application-secret" sbt
```

When the `sbt` shell is ready launch `run`. The server listens on `http://localhost:8080`.

## Useful links

- https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
- https://docs.github.com/en/developers/apps/guides/using-the-github-api-in-your-app
- https://docs.servicenow.com/bundle/paris-devops/page/product/enterprise-dev-ops/concept/dev-ops-github-apps-oauth-auth.html
- https://tapir.softwaremill.com/en/latest/examples.html
- https://github.com/softwaremill/tapir/blob/master/examples/src/main/scala/sttp/tapir/examples/MultipleEndpointsDocumentationHttp4sServer.scala
- https://github.com/softwaremill/tapir/blob/master/examples/src/main/scala/sttp/tapir/examples/HelloWorldAkkaServer.scala
