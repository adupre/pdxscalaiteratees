var n = 40,
    data = [];

for (var i = 0; i < n; i++)
    data[i] = { x: i, y: 0, changes: [] };

var margin = {top: 10, right: 10, bottom: 20, left: 40},
    width = 960 - margin.left - margin.right,
    height = 200 - margin.top - margin.bottom;

var x = d3.scale.linear()
    .domain([0, n - 3])
    .range([0, width]);

var y = d3.scale.linear()
    .domain([-2, 2])
    .range([height, 0]);

var line = d3.svg.line()
    .interpolate("basis")
    .x(function (d, i) {
        return x(i);
    }) // d.x
    .y(function (d, i) {
        return y(d.y);
    });

var flatLine = d3.svg.line()
    .x(function (d, i) {
        return x(i);
    }) // d.x
    .y(function (d, i) {
        return y(0);
    });

var svg = d3.select("body").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

svg.append("defs").append("clipPath")
    .attr("id", "clip")
    .append("rect")
    .attr("width", width)
    .attr("height", height);

svg.append("g")
    .attr("class", "y axis")
    .call(d3.svg.axis().scale(y).orient("left"));

var container = svg.append("g");

var path = container.append("g")
    .attr("clip-path", "url(#clip)")
    .append("path")
    .data([data])
    .attr("class", "line")
    .attr("d", flatLine);


var redraw = function () {
    var points = data
        .map(function (v, i) {
            return { id: v.x, x: i, y: v.y, changes: v.changes };
        })
        .filter(function (v) {
            return v.changes.length > 0;
        })

    var p = container.selectAll(".change")
        .data(points, function (d) {
            return d.id;
        }) // x here is the absolute time as key

    p.enter().append("line")
        .attr("class", "change")
        .attr("x1", function (d) {
            return x(d.x);
        })
        .attr("x2", function (d) {
            return x(d.x);
        })
        .attr("y1", y(2))
        .attr("y2", y(-2))

    p.transition()
        .duration(200) // this needs to match the feed speed
        .ease("linear")
        .attr("x1", function (d) {
            return x(d.x);
        })
        .attr("x2", function (d) {
            return x(d.x);
        })

    p.exit().remove()

    var l = container.selectAll(".label")
        .data(points, function (d) {
            return "label" + d.id;
        })

    l.enter().append("text")
        .attr("class", "label")
        // .attr("dy", -3)
        .attr("text-anchor", "end")
        .attr("transform", function (d) {
            return "translate(" + x(d.x) + ",0) rotate(-90)"
        })
        .text(function (d) {
            return d.changes
        });

    l.transition()
        .duration(200) // this needs to match the feed speed
        .ease("linear")
        .attr("transform", function (d) {
            return "translate(" + x(d.x) + ",0) rotate(-90)"
        })

    l.exit().remove()


    path
        .attr("d", line)
        .attr("transform", null)
        .transition()
        .duration(200) // this needs to match the feed speed
        .ease("linear")
        .attr("transform", "translate(" + x(-1) + ")")

}

function addDataPoint(point) {
    data.push(point);
    data.shift();
    redraw()
}

if (!!window.EventSource) {
    feed = new EventSource('/stream');

    feed.addEventListener('open', function (e) {
        console.log("Stream opened");
    }, false);

    feed.addEventListener('message', function (e) {
        addDataPoint(JSON.parse(e.data));
    }, false);
}


/*
 * Commands - Uses WebSockets
 */
function connect(url) {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
    var chatSocket = new WS(url);

    chatSocket.onmessage = function(event) {
        var data = JSON.parse(event.data);

        // Handle errors
        if(data.error) {
            chatSocket.close();
            alert("Closing websocket due to error " + error);
            return
        }

        $("#" + data.variable).val(data.value)
    };

    function sendUpdate(self) {
        var msg = {
            variable: $(self).attr("id"),
            value: parseFloat($(self).val()),
            by: $("#name").val()
        }
        chatSocket.send(JSON.stringify(msg))
        console.log(msg);
    }

    return {
        sendUpdate: sendUpdate
    };
}
