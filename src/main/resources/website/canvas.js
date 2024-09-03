import anime from './anime.js';

class Canvas {
    static {
        this.width = window.innerWidth;
        this.height = window.innerHeight;
        window.addEventListener('resize', () => {
            Canvas.width = window.innerWidth;
            Canvas.height = window.innerHeight;
        });
    }
    #element; #context; animations = [];
    constructor() {
        this.#element = document.createElement('canvas');
        this.#context = this.#element.getContext('2d');
        this.#element.width = window.innerWidth;
        this.#element.height = window.innerHeight;
        window.addEventListener('resize', () => {
            Canvas.width, this.#element.width = window.innerWidth;
            Canvas.height, this.#element.height = window.innerHeight;
        });
        document.addEventListener('click', mouse => {
            const particles = [];
            for( let i = 0; i < 32; i++ ) {
                const particle = new Particle({
                    x: mouse.x,
                    y: mouse.y,
                    radius: anime.random( 12, Particle.maxSize ),
                    fill: '#5865F2'
                });

                particles.push(particle);
            }

            const burst = new Animation({
                x: particle => {
                    return particle.x + anime.random( Particle.burstSize, -Particle.burstSize );
                },
                y: particle => {
                    return particle.y + anime.random( Particle.burstSize * 1.2, -Particle.burstSize * 1.2 );
                },
                easing: 'easeOutExpo',
                duration: anime.random(1000, 1400),
                radius: 0
            })
            burst.target( particles );
            
            const particle = new Particle({
                x: mouse.x,
                y: mouse.y,
                radius: 0,
                fill: '#5865F2',
                stroke: { color: '#5865F2', width: 3 },
                opacity: 0.4
            });

            const ripple = new Animation({
                opacity: 0,
                radius: Particle.burstSize * 0.85,
                easing: 'easeOutExpo',
                fill: 'white',
                duration: 800,
            })

            ripple.target( [ particle ] );

            this.animations.push( ripple, burst );
        });
        this.tick();
        return this.#element;
    }

    tick() {
        this.#context.clearRect(0, 0, Canvas.width, Canvas.height);

        this.animations.forEach((animation, index) => {
            if( animation.playing == false )
                animation.play( this.#context );
            if( !animation.alive )
                this.animations.splice(index, 1);
            animation.tick();
        });

        window.requestAnimationFrame(this.tick.bind(this));
    }
}

class Animation {
    #playing; #context
    constructor(params) {
        this.alive = true;
        this.targets = [];
        this.#playing = false;
        this.params = params;
        this.params.complete = () => {
            this.alive = false;
        }
    }

    target( items ) {
        if( !Array.isArray( items ) )
            return;
        for( let i = 0; i < items.length; i++ ) {
            const item = items[i];
            this.targets.push( item );
        }
    }

    play( context ) {
        this.params.targets = this.targets;
        this.animation = anime(this.params);
        this.#context = context;
        this.#playing = true;
    }

    tick() {
        this.animation.animatables.forEach(anime => {
            anime.target.draw( this.#context );
        })
    }

    get playing() {
        return this.#playing;
    }
}

class Particle {
    static {
        this.burstSize = Math.min( 200, ( Canvas.width * 0.4 ));
        this.maxSize = 36;
    }
    constructor(params) {
        Object.entries(params).forEach(pair => {
            const [key, value] = pair;
            this[key] = value;
        })
        this.alive = true;
    }

    draw( context ) {
        if( !this.alive )
            return;
        context.beginPath();
        context.globalAlpha = this.opacity == undefined ? 1 : Math.max(0, this.opacity);
        context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI, false);
        if( this.fill ) {
            context.fillStyle = this.fill;
            context.fill();
        }
        if( this.stroke ) {
            context.strokeStyle = this.stroke.color;
            context.lineWidth = this.stroke.width;
            context.stroke();
        }
        context.closePath();
        context.globalAlpha = 1;
    }
}

export default Canvas