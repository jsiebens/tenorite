var config = require('./gulp.conf.json');
var gulp = require('gulp');
var runSequence = require('run-sequence');
var $ = require('gulp-load-plugins')({
    lazy: true,
    pattern: ['gulp-*', 'gulp.*', 'del']
});

gulp.task('minify-css', function () {
    gulp.src(config.cssDependenciesFiles.concat(config.cssFiles))
        .pipe($.concat('style.css'))
        .pipe($.cleanCss())
        .pipe(gulp.dest(config.dest + '/css'));
});

gulp.task('copy-fonts', function () {
    gulp.src(config.base_fonts)
        .pipe(gulp.dest(config.dest + '/css'));
    gulp.src(config.fonts)
        .pipe(gulp.dest(config.dest + '/fonts'));
});

gulp.task('copy-images', function () {
    gulp.src(config.favicon)
        .pipe(gulp.dest(config.dest));
    gulp.src(config.imgs)
        .pipe(gulp.dest(config.dest + '/img'));
});

gulp.task('minify-js', function () {
    gulp.src(config.jsDependenciesFiles.concat(config.jsFiles))
        .pipe($.concat('scripts.js'))
        .pipe($.uglify())
        .pipe(gulp.dest(config.dest + '/js'));
});

gulp.task('build', function () {
    runSequence(
        'minify-css',
        'copy-images',
        'copy-fonts',
        'minify-js'
    );
});