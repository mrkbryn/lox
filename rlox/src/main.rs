mod token_type;

use std::env;
use std::fs;

fn run_file(path: &String) {
    println!("Running file! {}", path);
    let contents = fs::read_to_string(path)
        .expect("Failed to read file");
    println!("Contents: {}", contents);
}

fn run_prompt() {
    println!("Running prompt!")
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 2 {
        println!("Usage rlox [script]");
        std::process::exit(64);
    } else if args.len() == 2 {
        run_file(&args[1]);
    } else {
        run_prompt();
    }
}
