use std::collections::HashMap;
use std::num::NonZeroUsize;


pub struct Scanner {
    source: String,
    tokens: Vec<Token>,
    start: usize,
    current: usize,
    line: NonZeroUsize,
}

impl Scanner {
    pub const fn new(source: String) -> Self {
        Self {
            source,
            tokens: Vec::new(),
            start: 0,
            current: 0,
            line: NonZeroUsize::new(1).unwrap(),
        }
    }

    fn is_at_end(&self) -> bool {
        false
    }

    fn scan_token(&self) {

    }
}