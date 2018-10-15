# web-server-restful

## Usage

### Questions to Consider

- jkkj

# Project

## Introduction

This interview is designed to be completed in-office, and is supposed to be
challenging. Don't think of this so much as a challenge that can be *completed*
so much as an an opportunity to showcase a variety of skills and insight into
good system design.

Your output will be evaluated with the following criteria in mind:

- code is clear and readable
- interfaces between components are clearly defined
- concerns are separated whenever possible
- state is managed soundly
- appropriate trade-offs are made with respect to time
- attention to edge cases
- quality of questions asked during design & development process

## The Big Picture

Starcity wants to know how our spaces are utilized and provide these same useful
insights to our members. For example:

- Is the bathroom down the hall occupied?
- Is the front door of the building open or closed?
- How much activity is going on in the living room?
- Is the kitchen in use?
- How often and at what times do members use the media room?
- etc.

The way we'll accomplish this is by installing a variety of sensors throughout
the communal spaces that will send data to our servers, allowing us to answer
the above questions with some degree of accuracy.

Your task is to **write a system that simulates the flow of real sensor data
from a community, sends that information to a server, and displays the
information in a relevant fashion on a web front-end.**

Again, this challenge is designed in such a way that it will not be possible to
complete everything. Based on your skills and areas of expertise, feel free to
attempt to implement as much as you can and feel free to take a deep dive on a
specific area or areas as you see fit.

## Problem Scope

To limit the scope of this challenge, assume that we're dealing with a specific
community located at `229 Ellis Street` that has:

- a living room
- a media room
- two bathrooms
- a front door
- a back door

The "configuration" of sensors within these rooms/features is up to you.

The technical hiring manager will be available as a resource to you throughout
the duration of the challenge; please do not hesitate to ask questions.

## Part 1: Sensor Event Simulation

The first portion of this challenge is to simulate data coming from sensors.
We'll concern ourselves with *three* different types of sensors:

1. **Motion Sensors:** Detects motion within its field of view. Sends an event
   whenever it detects new motion.
2. **Light Sensors:** Detects the amount of incoming light. Sends an event
   whenever it detects a change in light above a given threshold.
3. **Door Sensors:** Sends an event when a door is either opened or closed.
    - *NOTE:* The sequence of events matters with doors; if the last event was
      an `opened` event, the next one should be a `closed` event (and vice
      versa).

Your task is to create a program that emits sensor events to simulate the
activity within the example community listed in **Problem Scope**. In more
detail:

- A single `sensor` should include at least:
  + unique `id` of the sensor
  + `type` of sensor
  + unique `id` of the room it's in
- A single `event` should include at least:
  + timestamp
  + unique `id` of the event
  + reference to the sensor that emitted it
- It should be possible to `start` and `stop` your system
- Your system should be **configurable** in the following ways:
  + Sensor events should be emitted **randomly** within a certain interval, e.g.
    *every 3 to 10 seconds*. This should be configurable on a per-sensor basis.
  + It should be possible to emit events in a variety of ways; for example, log
    them out, send them to an HTTP endpoint, put them on a queue, etc.

Feel free to take creative liberty with the above requirements--there are likely
additional pieces of information/functionality that are desirable.

## Part 2: Integration with a Web Stack

The second part of the challenge is to build a web server and client (service)
that receives sensor events and displays them in some meaningful way on the web.

At minimum, the following is expected:

- Your sensor event simulator should `POST` events to an endpoint on your server
- A client should be able to asynchronously receive those events (or data
  derived from them) in some fashion
- The following information should be shown:
  + For each non-bathroom, timestamp of most recent activity
  + For each bathroom, whether it's occupied or not
  + Whether the front & back doors are open or closed

### Other Considerations

- It's not necessary to persist events on the server, although it may be helpful.
- Can you show whether or not the lights on or off in a given room?
- *How much* activity is in a given room? Multiple motion sensors triggering in
  the same room may be a good way to detect this.
- Are there calculations in which the events from multiple types of sensors
  could be helpful?
- What kind of technology would be best to facilitate real-time communication of
  live event data between the server and client?
