var gulp = require("gulp");
var babel = require("gulp-babel");

gulp.task("build", function () {
    gulp.src("src/main/webapp/es2015/**/*.js")
        .pipe(babel({
            presets: ["es2015", "react"],
            plugins: ["transform-es2015-modules-amd"]
        }))
        .pipe(gulp.dest("src/main/webapp/js/"));
});

