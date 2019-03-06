// -----------
// EXPRESS SETUP
// -----------
const path = require("path");
const express = require("express");
const bodyParser = require("body-parser");
const mountApi = require("./api");

const isDeveloping = process.env.NODE_ENV !== "production";
const port = process.env.PORT || 8000;
const app = express();

// body parser must be set up before routes are attached
app.use(bodyParser.json());
mountApi(app, port);

if (isDeveloping) {
  const webpack = require("webpack");
  const webpackMiddleware = require("webpack-dev-middleware");
  const webpackHotMiddleware = require("webpack-hot-middleware");
  const config = require("./webpack.config.js");

  const lang = "en";

  // We will only use the english version during development
  const compiler = webpack(config.filter(x => x.name === lang));
  const middleware = webpackMiddleware(compiler, {
    publicPath: "/",
    contentBase: "src",
    stats: {
      colors: true,
      hash: false,
      timings: true,
      chunks: false,
      chunkModules: false,
      modules: false
    }
  });

  app.use(middleware);
  app.use(webpackHotMiddleware(compiler));
  app.get("*", function response(req, res) {
    res.write(
      middleware.fileSystem.readFileSync(
        path.join(__dirname, `dist/index.${lang}.html`)
      )
    );
    res.end();
  });
} else {
  app.use("/app/static", express.static(__dirname + "/dist"));
  app.get("/app/static/*", function response(req, res) {
    const lang = req.acceptsLanguages("de", "en") || "en";

    res.sendFile(path.join(__dirname, `dist/index.${lang}.html`));
  });
}

app.listen(port, "0.0.0.0", function onStart(err) {
  if (err) {
    console.log(err);
  }

  console.info("==> 🌎 Listening on port %s.", port);
});
